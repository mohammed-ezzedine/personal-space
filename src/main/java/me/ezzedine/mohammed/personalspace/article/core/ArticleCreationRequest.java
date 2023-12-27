package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class ArticleCreationRequest {
    @NonNull
    private String title;
    @NonNull
    private String categoryId;
    @NonNull
    private String description;
    @NonNull
    private String content;
    private String thumbnailImageUrl;
    @NonNull
    private List<String> keywords;
    @NonNull
    private Boolean hidden;
    @NonNull
    private String estimatedReadingTime;
}
