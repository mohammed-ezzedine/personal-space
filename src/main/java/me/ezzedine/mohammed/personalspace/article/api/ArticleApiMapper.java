package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.Article;

public class ArticleApiMapper {
    public static ArticleApiModel toApiModel(Article article) {
        return ArticleApiModel.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .content(article.getContent()).categoryId(article.getCategoryId()).thumbnailImageUrl(article.getThumbnailImageUrl())
                .keywords(article.getKeywords()).createdDate(article.getCreatedDate().toString()).estimatedReadingTime(article.getEstimatedReadingTime())
                .lastModifiedDate(article.getLastModifiedDate().toString()).hidden(article.isHidden()).build();
    }

    public static ArticleSummaryApiModel toSummaryApiModel(Article article) {
        return ArticleSummaryApiModel.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .categoryId(article.getCategoryId()).thumbnailImageUrl(article.getThumbnailImageUrl())
                .keywords(article.getKeywords()).createdDate(article.getCreatedDate().toString()).estimatedReadingTime(article.getEstimatedReadingTime())
                .lastModifiedDate(article.getLastModifiedDate().toString()).hidden(article.isHidden()).build();
    }
}
