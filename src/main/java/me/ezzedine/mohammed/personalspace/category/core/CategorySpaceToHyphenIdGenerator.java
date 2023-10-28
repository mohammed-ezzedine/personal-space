package me.ezzedine.mohammed.personalspace.category.core;

import org.springframework.stereotype.Service;

@Service
public class CategorySpaceToHyphenIdGenerator implements CategoryIdGenerator {

    @Override
    public String generate(String name) {
        return name.trim().toLowerCase().replace(' ', '-');
    }
}
