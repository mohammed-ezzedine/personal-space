package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.Article;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleApiTestUtil {

    public static final String ARTICLE_ID = "articleId";
    public static final String TITLE = "articleTitle";
    public static final String DESCRIPTION = "articleDescription";
    public static final String CONTENT = "articleContent";
    public static final String CATEGORY_ID = "articleCategoryId";
    public static final String ARTICLE_THUMBNAIL_IMAGE_URL = "articleThumbnailImageUrl";
    public static final String KEYWORD = "keyword";
    public static final String CREATED_DATE = "2023-12-24T12:33:42.411";
    public static final String LAST_MODIFIED_DATE = "2023-12-24T12:34:51.182";
    public static final boolean HIDDEN = true;
    public static final String ESTIMATED_READING_TIME = "estimatedReadingTime";

    public static Article getArticle() {
        return Article.builder().id(ARTICLE_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                .categoryId(CATEGORY_ID).thumbnailImageUrl(ARTICLE_THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD))
                .version(1L).createdDate(LocalDateTime.parse(CREATED_DATE)).lastModifiedDate(LocalDateTime.parse(LAST_MODIFIED_DATE))
                .hidden(HIDDEN).estimatedReadingTime(ESTIMATED_READING_TIME).build();
    }
}
