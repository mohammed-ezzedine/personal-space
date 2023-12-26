package me.ezzedine.mohammed.personalspace.article.api.highlight;

import me.ezzedine.mohammed.personalspace.article.api.advice.ArticleAlreadyHighlightedAdvice;
import me.ezzedine.mohammed.personalspace.article.api.advice.ArticleNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.article.api.advice.ArticleWasNotHighlightedAdvice;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static me.ezzedine.mohammed.personalspace.TestUtils.loadResource;
import static me.ezzedine.mohammed.personalspace.article.api.ArticleApiTestUtil.ARTICLE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        HighlightedArticlesController.class,
        ArticleAlreadyHighlightedAdvice.class,
        ArticleWasNotHighlightedAdvice.class,
        ArticleNotFoundAdvice.class
})
@EnableWebMvc
@AutoConfigureMockMvc
class HighlightedArticlesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticlesHighlightFetcher fetcher;

    @MockBean
    private ArticlesHighlightUpdater updater;

    @Nested
    @DisplayName("When adding an article to the highlight")
    class AddingArticleToHighlightsIntegrationTest {

        @Test
        @DisplayName("should return a not found status when the article does not exist")
        void should_return_a_not_found_status_when_the_article_does_not_exist() throws Exception {
            doThrow(ArticleNotFoundException.class).when(updater).addArticleToHighlights(ARTICLE_ID);

            mockMvc.perform(post("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return a bad request status when the article was already highlighted")
        void should_return_a_bad_request_status_when_the_article_was_already_highlighted() throws Exception {
            doThrow(ArticleAlreadyHighlightedException.class).when(updater).addArticleToHighlights(ARTICLE_ID);

            mockMvc.perform(post("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(post("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).addArticleToHighlights(ARTICLE_ID);
        }
    }

    @Nested
    @DisplayName("When removing an article from the highlight")
    class RemovingArticleFromHighlightsIntegrationTest {

        @Test
        @DisplayName("should return a not found status when the article does not exist")
        void should_return_a_not_found_status_when_the_article_does_not_exist() throws Exception {
            doThrow(ArticleNotFoundException.class).when(updater).removeArticleFromHighlights(ARTICLE_ID);

            mockMvc.perform(delete("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return a bad request status when the article was not highlighted")
        void should_return_a_bad_request_status_when_the_article_was_not_highlighted() throws Exception {
            doThrow(ArticleWasNotHighlightedException.class).when(updater).removeArticleFromHighlights(ARTICLE_ID);

            mockMvc.perform(delete("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(delete("/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).removeArticleFromHighlights(ARTICLE_ID);
        }
    }

    @Nested
    @DisplayName("When updating the article highlights")
    class UpdatingArticleHighlightsIntegrationTest {

        @Test
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(put("/articles/highlight")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/update_highlights_request.json")))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).updateArticlesHighlights(List.of(HighlightedArticle.builder().articleId("id").highlightRank(4).build()));
        }
    }

    @Nested
    @DisplayName("When fetching the summary of the highlighted articles")
    class FetchingHighlightedArticlesSummaryIntegrationTest {

        @Test
        @DisplayName("should return the list of articles summary on the happy path")
        void should_return_the_list_of_articles_on_the_happy_path() throws Exception {
            when(fetcher.getHighlightedArticlesSummary()).thenReturn(List.of(HighlightedArticle.builder().articleId(ARTICLE_ID).highlightRank(1).build()));
            String content = mockMvc.perform(get("/articles/highlight/summary"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/highlighted_articles_summary_response.json"), content);
        }
    }
}