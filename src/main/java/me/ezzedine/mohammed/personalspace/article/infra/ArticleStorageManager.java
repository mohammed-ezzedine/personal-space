package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticleStorage;
import me.ezzedine.mohammed.personalspace.article.core.ArticlesFetchCriteria;
import me.ezzedine.mohammed.personalspace.article.infra.highlight.HighlightedArticleEntity;
import me.ezzedine.mohammed.personalspace.article.infra.highlight.HighlightedArticleMongoRepository;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import me.ezzedine.mohammed.personalspace.util.pagination.PaginationCriteria;
import me.ezzedine.mohammed.personalspace.util.sort.SortingCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ArticleStorageManager implements ArticleStorage {

    private final ArticleMongoRepository repository;
    private final MongoTemplate mongoTemplate;
    private final HighlightedArticleMongoRepository highlightedArticleRepository;

    @Override
    public void save(Article article) {
        repository.save(toEntity(article));
    }

    @Override
    public Optional<Article> fetch(String id) {
        return repository.findById(id).map(ArticleStorageManager::fromEntity);
    }

    @Override
    public Page<Article> fetchAll(ArticlesFetchCriteria criteria) {
        Query query = new Query();

        criteria.getHighlighted().ifPresent(highlighted -> {
            List<String> highlightedArticlesIds = highlightedArticleRepository.findAll().stream()
                    .map(HighlightedArticleEntity::getArticleId)
                    .toList();

            if (highlighted) {
                query.addCriteria(Criteria.where("id").in(highlightedArticlesIds));
            } else {
                query.addCriteria(Criteria.where("id").nin(highlightedArticlesIds));
            }
        });

        criteria.getHidden().ifPresent(hidden -> query.addCriteria(Criteria.where("hidden").is(hidden)));

        criteria.getCategoryId().ifPresent(categoryId -> query.addCriteria(Criteria.where("categoryId").is(categoryId)));

        long totalNumberOfArticlesMatchingCriteria = mongoTemplate.count(query, ArticleEntity.class);

        criteria.getPaginationCriteria().ifPresent(paginationCriteria -> addPaginationCriteria(paginationCriteria, query));

        criteria.getSortingCriteria().ifPresent(sortingCriteria -> addSoringCriteria(sortingCriteria, query));

        List<Article> articles = mongoTemplate.find(query, ArticleEntity.class)
                .stream().map(ArticleStorageManager::fromEntity).toList();

        return Page.<Article>builder().items(articles).totalSize(totalNumberOfArticlesMatchingCriteria).build();
    }

    @Override
    public List<Article> fetchHighlightedArticles() {
        List<String> highlightedArticlesIds = highlightedArticleRepository.findAll().stream()
                .sorted(Comparator.comparing(HighlightedArticleEntity::getRank))
                .map(HighlightedArticleEntity::getArticleId).toList();

        Map<String, ArticleEntity> highlightedArticlesEntity = repository.findAllById(highlightedArticlesIds)
                .stream().collect(Collectors.toMap(ArticleEntity::getId, Function.identity()));

        return highlightedArticlesIds.stream()
                .map(highlightedArticlesEntity::get)
                .filter(Objects::nonNull)
                .map(ArticleStorageManager::fromEntity)
                .toList();
    }

    private static void addSoringCriteria(SortingCriteria sortingCriteria, Query query) {
        query.with(Sort.by(sortingCriteria.isAscendingOrder() ? Sort.Direction.ASC : Sort.Direction.DESC, sortingCriteria.getField()));
    }

    private static void addPaginationCriteria(PaginationCriteria paginationCriteria, Query query) {
        query.with(PageRequest.of(paginationCriteria.getStartingPageIndex(), paginationCriteria.getMaximumPageSize()));
    }

    private static ArticleEntity toEntity(Article article) {
        return ArticleEntity.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .content(article.getContent()).categoryId(article.getCategoryId()).thumbnailImageUrl(article.getThumbnailImageUrl())
                .keywords(article.getKeywords()).version(article.getVersion()).createdDate(article.getCreatedDate())
                .lastModifiedDate(article.getLastModifiedDate()).hidden(article.isHidden()).estimatedReadingTime(article.getEstimatedReadingTime()).build();
    }

    private static Article fromEntity(ArticleEntity articleEntity) {
        return Article.builder().id(articleEntity.getId()).content(articleEntity.getContent()).title(articleEntity.getTitle())
                .description(articleEntity.getDescription()).categoryId(articleEntity.getCategoryId())
                .thumbnailImageUrl(articleEntity.getThumbnailImageUrl()).keywords(articleEntity.getKeywords())
                .createdDate(articleEntity.getCreatedDate()).lastModifiedDate(articleEntity.getLastModifiedDate())
                .version(articleEntity.getVersion()).hidden(articleEntity.isHidden()).estimatedReadingTime(articleEntity.getEstimatedReadingTime()).build();
    }
}
