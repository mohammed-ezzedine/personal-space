package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import me.ezzedine.mohammed.personalspace.category.core.Category;

@Data
@Builder
public class Article {
    @NonNull
    private String id;
    @NonNull
    private Category category;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private String content;
}
