package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Article {
    @NonNull
    private String id;
    @NonNull
    private String categoryId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private String content;
}
