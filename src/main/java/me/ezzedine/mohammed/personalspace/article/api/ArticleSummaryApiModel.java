package me.ezzedine.mohammed.personalspace.article.api;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ArticleSummaryApiModel {
    private String id;
    private String title;
    private String description;
    private String categoryId;
    private String thumbnailImageUrl;
    private String createdDate;
    private String lastModifiedDate;
    private Boolean hidden;
    private List<String> keywords;
    private String estimatedReadingTime;
}
