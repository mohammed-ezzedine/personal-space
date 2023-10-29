package me.ezzedine.mohammed.personalspace.article.core;

import java.util.Optional;

public interface ArticleStorage {
    void save(Article article);
    Optional<Article> fetch(String id);
}
