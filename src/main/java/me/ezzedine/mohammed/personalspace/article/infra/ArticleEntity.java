package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> keywords;
    private boolean hidden;
    private String estimatedReadingTime;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @Version
    private Long version;
}
