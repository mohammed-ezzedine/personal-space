package me.ezzedine.mohammed.personalspace.category.core;

import org.springframework.stereotype.Service;

@Service
public class ArticleCategorySpaceToHyphenIdGenerator implements ArticleCategoryIdGenerator {

    @Override
    public String generate(String name) {
        return name.trim().toLowerCase().replace(' ', '-');
    }
}
