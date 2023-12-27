package me.ezzedine.mohammed.personalspace.article.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateApiRequest {
    private String categoryId;
    private String title;
    private String description;
    private String content;
    private String thumbnailImageUrl;
    private List<String> keywords;
    private Boolean hidden;
    private String estimatedReadingTime;
}
