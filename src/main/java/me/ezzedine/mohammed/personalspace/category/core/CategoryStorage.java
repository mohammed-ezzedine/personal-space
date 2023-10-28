package me.ezzedine.mohammed.personalspace.category.core;

import java.util.List;
import java.util.Optional;

public interface CategoryStorage {
    boolean categoryExists(String id);
    Optional<Category> fetch(String id);
    List<Category> fetchAll();
    Optional<Category> fetchCategoryWithHighestOrder();
    void persist(Category category);
    void delete(String id);
}
