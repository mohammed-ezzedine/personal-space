package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;

public interface ArticleEditor {
    void edit(ArticleUpdateRequest request) throws ArticleNotFoundException, CategoryNotFoundException;
}
