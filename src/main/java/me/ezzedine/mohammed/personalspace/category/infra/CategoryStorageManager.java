package me.ezzedine.mohammed.personalspace.category.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.Category;
import me.ezzedine.mohammed.personalspace.category.core.CategoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryStorageManager implements CategoryStorage {

    private final CategoryRepository repository;

    @Override
    public boolean categoryExists(String id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Category> fetch(String id) {
        return repository.findById(id).map(CategoryStorageManager::fromEntity);
    }

    @Override
    public List<Category> fetchAllOrderedByOrder() {
        return repository.findAllByOrderByOrderAsc().stream().map(CategoryStorageManager::fromEntity).toList();
    }

    @Override
    public Optional<Category> fetchCategoryWithHighestOrder() {
        return repository.findFirstByOrderByOrderDesc().map(CategoryStorageManager::fromEntity);
    }

    @Override
    public void persist(Category category) {
        repository.save(toEntity(category));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    private static CategoryEntity toEntity(Category category) {
        return CategoryEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .canBeDeleted(category.canBeDeleted())
                .order(category.getOrder())
                .build();
    }

    private static Category fromEntity(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .canBeDeleted(entity.canBeDeleted())
                .order(entity.getOrder())
                .build();
    }
}
