package me.ezzedine.mohammed.personalspace.article.api.highlight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleHighlightApiModel {
    private String articleId;
    private int rank;
}
