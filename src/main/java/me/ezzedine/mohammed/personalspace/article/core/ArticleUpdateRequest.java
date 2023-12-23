package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;

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
}
