package me.ezzedine.mohammed.personalspace.article.core;

public interface ArticleFetcher {
    Article fetch(String id) throws ArticleNotFoundException;
}
