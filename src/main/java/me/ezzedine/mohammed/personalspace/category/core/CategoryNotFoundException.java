package me.ezzedine.mohammed.personalspace.category.core;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String id) {
        super(String.format("A category with ID '%s' does not exist.", id));
    }
}
