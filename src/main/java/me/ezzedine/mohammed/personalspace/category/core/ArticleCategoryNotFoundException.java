package me.ezzedine.mohammed.personalspace.category.core;

public class ArticleCategoryNotFoundException extends Exception {
    public ArticleCategoryNotFoundException(String id) {
        super(String.format("An article category with ID '%s' does not exist.", id));
    }
}
