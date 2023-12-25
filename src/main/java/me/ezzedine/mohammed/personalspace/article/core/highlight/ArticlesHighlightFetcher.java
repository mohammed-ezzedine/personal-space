package me.ezzedine.mohammed.personalspace.article.core.highlight;

import me.ezzedine.mohammed.personalspace.article.core.Article;

import java.util.List;

public interface ArticlesHighlightFetcher {
    List<Article> getHighlightedArticles();
    List<HighlightedArticle> getHighlightedArticlesSummary();
}
