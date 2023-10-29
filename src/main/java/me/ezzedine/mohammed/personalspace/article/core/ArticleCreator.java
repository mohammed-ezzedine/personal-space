package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;

public interface ArticleCreator {
    ArticleCreationResult create(ArticleCreationRequest request) throws CategoryNotFoundException;
}
