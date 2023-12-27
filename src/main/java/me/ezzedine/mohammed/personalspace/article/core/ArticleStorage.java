package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.util.pagination.Page;

import java.util.List;
import java.util.Optional;

public interface ArticleStorage {
    void save(Article article);
    Optional<Article> fetch(String id);
    Page<Article> fetchAll(ArticlesFetchCriteria criteria);
    List<Article> fetchHighlightedArticles();
}
