package me.ezzedine.mohammed.personalspace.article.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleApiModel {
    private String id;
    private String title;
    private String description;
    private String content;
    private String categoryId;
    private String thumbnailImageUrl;
}
