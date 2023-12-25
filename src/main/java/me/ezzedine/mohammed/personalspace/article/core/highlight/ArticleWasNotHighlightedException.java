package me.ezzedine.mohammed.personalspace.article.core.highlight;

public class ArticleWasNotHighlightedException extends Exception {
    public ArticleWasNotHighlightedException(String articleId) {
        super(String.format("Article with ID {} was not highlighted.", articleId));
    }
}
