package me.ezzedine.mohammed.personalspace.article.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ArticleApiModel extends ArticleSummaryApiModel {
    private String content;
}
