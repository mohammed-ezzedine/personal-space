package me.ezzedine.mohammed.personalspace.category.core;

public class CategoryIdAlreadyExistsException extends Exception {
    public CategoryIdAlreadyExistsException(String name) {
        super(String.format("Category '%s' conflicts with another one with an exact or similar name that already exists", name));
    }
}
