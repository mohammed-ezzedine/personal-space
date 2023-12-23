package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleCreationRequest {
    private String title;
    private String categoryId;
    private String description;
    private String content;
    private String thumbnailImageUrl;
}
