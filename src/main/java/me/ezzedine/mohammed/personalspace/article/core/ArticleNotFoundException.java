package me.ezzedine.mohammed.personalspace.article.core;

public class ArticleNotFoundException extends Exception {
    public ArticleNotFoundException(String id) {
        super(String.format("Article with ID '%s' does not exist.", id));
    }
}
