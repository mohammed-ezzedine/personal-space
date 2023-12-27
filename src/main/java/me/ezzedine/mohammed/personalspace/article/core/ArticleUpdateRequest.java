package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class ArticleUpdateRequest {
    private String id;
    private String title;
    private String categoryId;
    private String description;
    private String content;
    private String thumbnailImageUrl;
    private List<String> keywords;
    private Boolean hidden;
    private String estimatedReadingTime;

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<String> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<String> getThumbnailImageUrl() {
        return Optional.ofNullable(thumbnailImageUrl);
    }

    public Optional<List<String>> getKeywords() {
        return Optional.ofNullable(keywords);
    }

    public Optional<Boolean> getHidden() {
        return Optional.ofNullable(hidden);
    }

    public Optional<String> getEstimatedReadingTime() {
        return Optional.ofNullable(estimatedReadingTime);
    }
}
