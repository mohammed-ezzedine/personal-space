package me.ezzedine.mohammed.personalspace.category.core;

import java.util.List;
import java.util.Optional;

public interface ArticleCategoryStorage {
    boolean categoryExists(String id);
    Optional<ArticleCategory> fetch(String id);
    List<ArticleCategory> fetchAll();
    void persist(ArticleCategory category);
    void delete(String id);
}
