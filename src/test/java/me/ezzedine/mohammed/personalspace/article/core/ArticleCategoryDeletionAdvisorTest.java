package me.ezzedine.mohammed.personalspace.article.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArticleCategoryDeletionAdvisorTest {

    public static final String CATEGORY_ID = UUID.randomUUID().toString();
    private ArticleStorage articleStorage;
    private ArticleCategoryDeletionAdvisor deletionAdvisor;

    @BeforeEach
    void setUp() {
        articleStorage = mock(ArticleStorage.class);
        deletionAdvisor = new ArticleCategoryDeletionAdvisor(articleStorage);
    }

    @Test
    @DisplayName("the user should not be able to delete a category that has at least one article linked to it")
    void the_user_should_not_be_able_to_delete_a_category_that_has_at_least_one_article_linked_to_it() {
        Article article = getArticleMock(UUID.randomUUID().toString());
        when(articleStorage.fetchByCategory(CATEGORY_ID)).thenReturn(List.of(article));
        assertFalse(deletionAdvisor.canDeleteCategory(CATEGORY_ID).isAllowed());
    }

    @Test
    @DisplayName("the user should get a message including the list of conflicting articles IDs when the deletion is rejected")
    void the_user_should_get_a_message_including_the_list_of_conflicting_articles_IDs_when_the_deletion_is_rejected() {
        String firstArticleId = UUID.randomUUID().toString();
        Article firstArticle = getArticleMock(firstArticleId);
        String secondArticleId = UUID.randomUUID().toString();
        Article secondArticle = getArticleMock(secondArticleId);

        when(articleStorage.fetchByCategory(CATEGORY_ID)).thenReturn(List.of(firstArticle, secondArticle));
        assertTrue(deletionAdvisor.canDeleteCategory(CATEGORY_ID).getMessage().isPresent());
        String message = "Category cannot be deleted since it is linked to the following articles: [" + firstArticleId + ", " + secondArticleId + "]";
        assertEquals(message, deletionAdvisor.canDeleteCategory(CATEGORY_ID).getMessage().get());
    }

    @Test
    @DisplayName("the user should be able to delete a category that is not linked to any article")
    void the_user_should_be_able_to_delete_a_category_that_is_not_linked_to_any_article() {
        when(articleStorage.fetchByCategory(CATEGORY_ID)).thenReturn(Collections.emptyList());
        assertTrue(deletionAdvisor.canDeleteCategory(CATEGORY_ID).isAllowed());
    }

    private Article getArticleMock(String id) {
        Article article = mock(Article.class);
        when(article.getId()).thenReturn(id);
        return article;
    }
}