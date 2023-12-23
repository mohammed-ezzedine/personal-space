package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntity {
    private String id;
    private String title;
    private String description;
    private String content;
    private String categoryId;
    private String thumbnailImageUrl;
}
