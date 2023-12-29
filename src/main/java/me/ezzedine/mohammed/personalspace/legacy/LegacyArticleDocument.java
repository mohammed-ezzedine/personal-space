package me.ezzedine.mohammed.personalspace.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyArticleDocument {
    private String id;
    private String title;
    private String description;
    private String estimatedReadingTime;
    private String categoryId;
    private String thumbnailImageUrl;
    private String content;
    private String createdDate;
    private String lastModifiedDate;
    private List<String> keywords;
}
