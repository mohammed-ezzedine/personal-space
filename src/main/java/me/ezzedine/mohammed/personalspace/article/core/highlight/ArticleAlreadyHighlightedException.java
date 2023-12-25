package me.ezzedine.mohammed.personalspace.article.core.highlight;

public class ArticleAlreadyHighlightedException extends Exception {
    public ArticleAlreadyHighlightedException(String articleId) {
        super(String.format("Article with ID %s is already highlighted", articleId));
    }
}
