package me.ezzedine.mohammed.personalspace.article.core;

import java.util.List;

public interface ArticleFetcher {
    Article fetch(String id) throws ArticleNotFoundException;
    List<Article> fetchAll();
}
