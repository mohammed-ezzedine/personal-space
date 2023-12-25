package me.ezzedine.mohammed.personalspace.article.core.highlight;

import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.ArticleStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticlesHighlightServiceTest {

    public static final String FIRST_ARTICLE_ID = UUID.randomUUID().toString();
    public static final int FIRST_RANK = 1;
    public static final String SECOND_ARTICLE_ID = UUID.randomUUID().toString();
    public static final int SECOND_RANK = 2;
    public static final String THIRD_ARTICLE_ID = UUID.randomUUID().toString();
    public static final int THIRD_RANK = 3;
    private ArticlesHighlightStorage highlightStorage;
    private ArticlesHighlightService service;
    private ArticleStorage articleStorage;

    @BeforeEach
    void setUp() {
        highlightStorage = mock(ArticlesHighlightStorage.class);
        articleStorage = mock(ArticleStorage.class);
        service = new ArticlesHighlightService(highlightStorage, articleStorage);

        when(articleStorage.fetch(any())).thenReturn(Optional.of(mock(Article.class)));
        when(highlightStorage.getHighlightedArticles()).thenReturn(List.of(getArticle(FIRST_ARTICLE_ID, FIRST_RANK), getArticle(SECOND_ARTICLE_ID, SECOND_RANK)));
    }

    @Nested
    @DisplayName("When updating the list of highlighted articles")
    class UpdatingArticlesHighlightsTest {

        @Test
        @DisplayName("the new list should be stored in the storage")
        void the_new_list_should_be_stored_in_the_storage() {
            HighlightedArticle article = mock(HighlightedArticle.class);
            service.updateArticlesHighlights(List.of(article));
            verify(highlightStorage).save(List.of(article));
        }

        @Test
        @DisplayName("should skip non existing articles")
        void should_skip_non_existing_articles() {
            when(articleStorage.fetch(FIRST_ARTICLE_ID)).thenReturn(Optional.empty());
            HighlightedArticle firstHighlight = HighlightedArticle.builder().articleId(FIRST_ARTICLE_ID).highlightRank(1).build();
            HighlightedArticle secondHighlight = HighlightedArticle.builder().articleId(SECOND_ARTICLE_ID).highlightRank(2).build();
            service.updateArticlesHighlights(List.of(firstHighlight, secondHighlight));
            verify(highlightStorage).save(List.of(secondHighlight));
        }

        @Test
        @DisplayName("should disregard the second and more occurrences of the same article id")
        void should_disregard_the_second_and_more_occurrences_of_the_same_article_id() {
            HighlightedArticle firstHighlight = HighlightedArticle.builder().articleId(FIRST_ARTICLE_ID).highlightRank(1).build();
            HighlightedArticle secondHighlight = HighlightedArticle.builder().articleId(FIRST_ARTICLE_ID).highlightRank(2).build();
            service.updateArticlesHighlights(List.of(firstHighlight, secondHighlight));
            verify(highlightStorage).save(List.of(firstHighlight));
        }

    }
    @Nested
    @DisplayName("When adding an article to the list of highlighted articles")
    class HighlightingArticleTest {

        @Test
        @DisplayName("should throw an exception if the article does not exist")
        void should_throw_an_exception_if_the_article_does_not_exist() {
            when(articleStorage.fetch(FIRST_ARTICLE_ID)).thenReturn(Optional.empty());
            assertThrows(ArticleNotFoundException.class, () -> service.addArticleToHighlights(FIRST_ARTICLE_ID));
        }

        @Test
        @DisplayName("should throw an exception if the article is already highlighted")
        void should_throw_an_exception_if_the_article_is_already_highlighted() {
            assertThrows(ArticleAlreadyHighlightedException.class, () -> service.addArticleToHighlights(FIRST_ARTICLE_ID));
        }

        @Test
        @DisplayName("the article should be added to the end of the list of highlighted articles")
        void the_article_should_be_added_to_the_end_of_the_list_of_highlighted_articles() throws ArticleAlreadyHighlightedException, ArticleNotFoundException {
            service.addArticleToHighlights(THIRD_ARTICLE_ID);

            verify(highlightStorage).save(List.of(getArticle(FIRST_ARTICLE_ID, FIRST_RANK),
                    getArticle(SECOND_ARTICLE_ID, SECOND_RANK), getArticle(THIRD_ARTICLE_ID, THIRD_RANK)));
        }
    }

    @Nested
    @DisplayName("When removing an article from the list of highlighted articles")
    class RemovingArticleFromHighlightsTest {
        @Test
        @DisplayName("should throw an exception if the article was not highlighted")
        void should_throw_an_exception_if_the_article_was_not_highlighted() {
            assertThrows(ArticleWasNotHighlightedException.class, () -> service.removeArticleFromHighlights(THIRD_ARTICLE_ID));
        }

        @Test
        @DisplayName("should throw an exception if the article does not exist")
        void should_throw_an_exception_if_the_article_does_not_exist() {
            when(articleStorage.fetch(FIRST_ARTICLE_ID)).thenReturn(Optional.empty());
            assertThrows(ArticleNotFoundException.class, () -> service.removeArticleFromHighlights(FIRST_ARTICLE_ID));
        }

        @Test
        @DisplayName("should remove the article from the highlights without adjusting the ranks if it were the last one")
        void should_remove_the_article_from_the_highlights_without_adjusting_the_ranks_if_it_were_the_last_one() throws ArticleWasNotHighlightedException, ArticleNotFoundException {
            service.removeArticleFromHighlights(SECOND_ARTICLE_ID);
            verify(highlightStorage).save(List.of(getArticle(FIRST_ARTICLE_ID, FIRST_RANK)));
        }

        @Test
        @DisplayName("should adjust the ranks after removing the article if it were not the last one")
        void should_adjust_the_ranks_after_removing_the_article_if_it_were_not_the_last_one() throws ArticleWasNotHighlightedException, ArticleNotFoundException {
            service.removeArticleFromHighlights(FIRST_ARTICLE_ID);
            verify(highlightStorage).save(List.of(getArticle(SECOND_ARTICLE_ID, FIRST_RANK)));
        }
    }

    @Nested
    @DisplayName("When fetching the list of highlighted articles")
    class FetchingHighlightedArticlesTest {
        @Test
        @DisplayName("should return an empty list when no articles are highlighted")
        void should_return_an_empty_list_when_no_articles_are_highlighted() {
            when(highlightStorage.getHighlightedArticles()).thenReturn(Collections.emptyList());
            assertTrue(service.getHighlightedArticles().isEmpty());
        }

        @Test
        @DisplayName("should return the details of the highlighted articles")
        void should_return_the_details_of_the_highlighted_articles() {
            Article firstArticle = mock(Article.class);
            Article secondArticle = mock(Article.class);
            when(articleStorage.fetch(FIRST_ARTICLE_ID)).thenReturn(Optional.of(firstArticle));
            when(articleStorage.fetch(SECOND_ARTICLE_ID)).thenReturn(Optional.of(secondArticle));

            List<Article> highlightedArticles = service.getHighlightedArticles();
            assertEquals(2, highlightedArticles.size());
            assertEquals(firstArticle, highlightedArticles.get(0));
            assertEquals(secondArticle, highlightedArticles.get(1));
        }

        @Test
        @DisplayName("should skip articles that are not found")
        void should_skip_articles_that_are_not_found() {
            Article firstArticle = mock(Article.class);
            when(articleStorage.fetch(FIRST_ARTICLE_ID)).thenReturn(Optional.of(firstArticle));
            when(articleStorage.fetch(SECOND_ARTICLE_ID)).thenReturn(Optional.empty());

            List<Article> highlightedArticles = service.getHighlightedArticles();
            assertEquals(1, highlightedArticles.size());
            assertEquals(firstArticle, highlightedArticles.get(0));
        }
    }

    @Nested
    @DisplayName("When fetching the summary of the list of highlighted articles")
    class FetchingHighlightedArticlesSummaryTest {
        @Test
        @DisplayName("should return an empty list when no articles are highlighted")
        void should_return_an_empty_list_when_no_articles_are_highlighted() {
            when(highlightStorage.getHighlightedArticles()).thenReturn(Collections.emptyList());
            assertTrue(service.getHighlightedArticlesSummary().isEmpty());
        }

        @Test
        @DisplayName("should return the summary of the highlighted articles")
        void should_return_the_summary_of_the_highlighted_articles() {
            List<HighlightedArticle> highlightedArticles = service.getHighlightedArticlesSummary();
            assertEquals(2, highlightedArticles.size());
            assertEquals(getArticle(FIRST_ARTICLE_ID, FIRST_RANK), highlightedArticles.get(0));
            assertEquals(getArticle(SECOND_ARTICLE_ID, SECOND_RANK), highlightedArticles.get(1));
        }
    }

    private static HighlightedArticle getArticle(String articleId, int rank) {
        return HighlightedArticle.builder().articleId(articleId).highlightRank(rank).build();
    }
}