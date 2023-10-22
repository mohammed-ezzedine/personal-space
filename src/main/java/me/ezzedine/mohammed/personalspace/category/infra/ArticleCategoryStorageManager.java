package me.ezzedine.mohammed.personalspace.category.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategory;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleCategoryStorageManager implements ArticleCategoryStorage {

    private final ArticleCategoryRepository repository;

    @Override
    public boolean categoryExists(String id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<ArticleCategory> fetch(String id) {
        return repository.findById(id).map(ArticleCategoryStorageManager::fromEntity);
    }

    @Override
    public List<ArticleCategory> fetchAll() {
        return repository.findAll().stream().map(ArticleCategoryStorageManager::fromEntity).toList();
    }

    @Override
    public void persist(ArticleCategory category) {
        repository.save(toEntity(category));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    private static ArticleCategoryEntity toEntity(ArticleCategory category) {
        return ArticleCategoryEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .canBeDeleted(category.canBeDeleted())
                .build();
    }

    private static ArticleCategory fromEntity(ArticleCategoryEntity entity) {
        return ArticleCategory.builder()
                .id(entity.getId())
                .name(entity.getName())
                .canBeDeleted(entity.canBeDeleted())
                .build();
    }
}
