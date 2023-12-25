package me.ezzedine.mohammed.personalspace.article.core.highlight;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;

import java.util.List;

public interface ArticlesHighlightUpdater {
    void addArticleToHighlights(String articleId) throws ArticleAlreadyHighlightedException, ArticleNotFoundException;
    void removeArticleFromHighlights(String articleId) throws ArticleWasNotHighlightedException, ArticleNotFoundException;
    void updateArticlesHighlights(List<HighlightedArticle> articles);
}
