package me.ezzedine.mohammed.personalspace.category.core;

public class ArticleCategoryIdAlreadyExistsException extends Exception {
    public ArticleCategoryIdAlreadyExistsException(String name) {
        super(String.format("Article category '%s' conflicts with another one with an exact or similar name that already exists", name));
    }
}
