package me.ezzedine.mohammed.personalspace.article.core.highlight;

import java.util.List;

public interface ArticlesHighlightStorage {
    void save(List<HighlightedArticle> highlightedArticles);
    List<HighlightedArticle> getArticleHighlightsSummary();
}
