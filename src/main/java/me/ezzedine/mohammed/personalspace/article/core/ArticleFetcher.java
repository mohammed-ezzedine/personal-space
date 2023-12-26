package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.util.pagination.Page;

public interface ArticleFetcher {
    Article fetch(String id) throws ArticleNotFoundException;
    Page<Article> fetchAll(ArticlesFetchCriteria criteria);
}
