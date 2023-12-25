package me.ezzedine.mohammed.personalspace.article.core.highlight;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HighlightedArticle {
    private String articleId;
    private int highlightRank;
}
