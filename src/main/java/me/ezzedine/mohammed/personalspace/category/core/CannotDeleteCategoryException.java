package me.ezzedine.mohammed.personalspace.category.core;

public class CannotDeleteCategoryException extends Exception {
    public CannotDeleteCategoryException(String id) {
        super(String.format("Category with ID '%s' is marked not to be deleted.", id));
    }
}
