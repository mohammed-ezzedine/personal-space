package me.ezzedine.mohammed.personalspace.article.api;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreationApiRequest {
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private String content;
    @NonNull
    private String categoryId;
    private String thumbnailImageUrl;
    @NonNull
    private List<String> keywords;
}
