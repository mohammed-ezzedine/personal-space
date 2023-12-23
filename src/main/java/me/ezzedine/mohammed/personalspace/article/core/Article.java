package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

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
    private String thumbnailImageUrl;
    @Builder.Default
    private List<String> keywords = new ArrayList<>();
}
