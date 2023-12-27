package me.ezzedine.mohammed.personalspace.article.infra.highlight;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticlesHighlightStorage;
import me.ezzedine.mohammed.personalspace.article.core.highlight.HighlightedArticle;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticlesHighlightStorageManager implements ArticlesHighlightStorage {

    private final HighlightedArticleMongoRepository repository;

    @Override
    public void save(List<HighlightedArticle> highlightedArticles) {
        repository.deleteAll();
        repository.saveAll(highlightedArticles.stream().map(ArticlesHighlightStorageManager::toEntity).toList());
    }

    @Override
    public List<HighlightedArticle> getArticleHighlightsSummary() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(HighlightedArticleEntity::getRank))
                .map(ArticlesHighlightStorageManager::fromEntity).toList();
    }

    private static HighlightedArticle fromEntity(HighlightedArticleEntity a) {
        return HighlightedArticle.builder().articleId(a.getArticleId()).highlightRank(a.getRank()).build();
    }

    private static HighlightedArticleEntity toEntity(HighlightedArticle a) {
        return HighlightedArticleEntity.builder().articleId(a.getArticleId()).rank(a.getHighlightRank()).build();
    }
}
