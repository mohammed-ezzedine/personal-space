package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticleStorage;
import org.springframework.stereotype.Repository;

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

    private static ArticleEntity toEntity(Article article) {
        return ArticleEntity.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .content(article.getContent()).categoryId(article.getCategoryId()).build();
    }

    private static Article fromEntity(ArticleEntity articleEntity) {
        return Article.builder().id(articleEntity.getId()).content(articleEntity.getContent()).title(articleEntity.getTitle())
                .description(articleEntity.getDescription()).categoryId(articleEntity.getCategoryId()).build();
    }
}
