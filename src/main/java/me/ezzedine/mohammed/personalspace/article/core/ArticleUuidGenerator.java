package me.ezzedine.mohammed.personalspace.article.core;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArticleUuidGenerator implements ArticleIdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
