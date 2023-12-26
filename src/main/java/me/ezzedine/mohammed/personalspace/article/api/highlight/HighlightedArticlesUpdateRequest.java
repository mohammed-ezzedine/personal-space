package me.ezzedine.mohammed.personalspace.article.api.highlight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HighlightedArticlesUpdateRequest {
    private List<ArticleHighlightApiModel> articles;
}
