package me.ezzedine.mohammed.personalspace.category.core;

public class CannotDeleteArticleCategoryException extends Exception {
    public CannotDeleteArticleCategoryException(String id) {
        super(String.format("Category with ID '%s' is marked not to be deleted.", id));
    }
}
