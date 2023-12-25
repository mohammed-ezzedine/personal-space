package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticleStorage;
import me.ezzedine.mohammed.personalspace.util.pagination.FetchCriteria;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleStorageManager implements ArticleStorage {

    private final ArticleMongoRepository repository;

    @Override
    public void save(Article article) {
        repository.save(toEntity(article));
    }

    @Override
    public Optional<Article> fetch(String id) {
        return repository.findById(id).map(ArticleStorageManager::fromEntity);
    }

    @Override
    public Page<Article> fetchAll(FetchCriteria criteria) {
        long numberOfSkippedArticles = (long) criteria.getStartingPageIndex() * criteria.getMaximumPageSize();
        long totalNumberOfArticles = repository.count();
        List<Article> articles = repository.findAll().stream()
                .sorted((a,b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                .skip(numberOfSkippedArticles)
                .limit(criteria.getMaximumPageSize())
                .map(ArticleStorageManager::fromEntity)
                .toList();
        return Page.<Article>builder().items(articles).totalSize(totalNumberOfArticles).build();
    }

    @Override
    public List<Article> fetchByCategory(String categoryId) {
        return repository.findByCategoryId(categoryId).stream().map(ArticleStorageManager::fromEntity).toList();
    }

    private static ArticleEntity toEntity(Article article) {
        return ArticleEntity.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .content(article.getContent()).categoryId(article.getCategoryId()).thumbnailImageUrl(article.getThumbnailImageUrl())
                .keywords(article.getKeywords()).version(article.getVersion()).createdDate(article.getCreatedDate())
                .lastModifiedDate(article.getLastModifiedDate()).hidden(article.isHidden()).build();
    }

    private static Article fromEntity(ArticleEntity articleEntity) {
        return Article.builder().id(articleEntity.getId()).content(articleEntity.getContent()).title(articleEntity.getTitle())
                .description(articleEntity.getDescription()).categoryId(articleEntity.getCategoryId())
                .thumbnailImageUrl(articleEntity.getThumbnailImageUrl()).keywords(articleEntity.getKeywords())
                .createdDate(articleEntity.getCreatedDate()).lastModifiedDate(articleEntity.getLastModifiedDate())
                .version(articleEntity.getVersion()).hidden(articleEntity.isHidden()).build();
    }
}
