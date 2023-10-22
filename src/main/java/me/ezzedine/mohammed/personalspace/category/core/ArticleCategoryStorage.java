package me.ezzedine.mohammed.personalspace.category.core;

import java.util.List;

public interface ArticleCategoryStorage {
    boolean categoryExists(String id);
    List<ArticleCategory> fetchAll();
    void persist(ArticleCategory category);
    void delete(String id);
}
