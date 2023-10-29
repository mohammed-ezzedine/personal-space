package me.ezzedine.mohammed.personalspace.article.api;

import lombok.*;

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
}
