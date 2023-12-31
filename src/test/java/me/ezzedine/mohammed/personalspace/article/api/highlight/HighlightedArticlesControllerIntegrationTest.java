package me.ezzedine.mohammed.personalspace.article.api.highlight;

import me.ezzedine.mohammed.personalspace.SecurityTestConfiguration;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static me.ezzedine.mohammed.personalspace.TestUtils.loadResource;
import static me.ezzedine.mohammed.personalspace.article.api.ArticleApiTestUtil.ARTICLE_ID;
import static me.ezzedine.mohammed.personalspace.article.api.ArticleApiTestUtil.getArticle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        HighlightedArticlesController.class,
        ArticleAlreadyHighlightedAdvice.class,
        ArticleWasNotHighlightedAdvice.class,
        ArticleNotFoundAdvice.class
})
@Import(SecurityTestConfiguration.class)
@EnableWebMvc
@AutoConfigureMockMvc(addFilters = false)
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
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot add articles to the highlight")
        void un_authenticated_users_cannot_add_articles_to_the_highlight() {
            assertThrows(Exception.class, () -> mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot add articles to the highlight")
        void authenticated_users_that_are_not_admins_cannot_add_articles_to_the_highlight() {
            assertThrows(Exception.class, () -> mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("admins can add articles to the highlight")
        void admins_can_add_articles_to_the_highlight() {
            assertDoesNotThrow(() -> mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a not found status when the article does not exist")
        void should_return_a_not_found_status_when_the_article_does_not_exist() throws Exception {
            doThrow(ArticleNotFoundException.class).when(updater).addArticleToHighlights(ARTICLE_ID);

            mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a bad request status when the article was already highlighted")
        void should_return_a_bad_request_status_when_the_article_was_already_highlighted() throws Exception {
            doThrow(ArticleAlreadyHighlightedException.class).when(updater).addArticleToHighlights(ARTICLE_ID);

            mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(post("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).addArticleToHighlights(ARTICLE_ID);
        }
    }

    @Nested
    @DisplayName("When removing an article from the highlight")
    class RemovingArticleFromHighlightsIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot remove articles from the highlight")
        void un_authenticated_users_cannot_remove_articles_from_the_highlight() {
            assertThrows(Exception.class, () -> mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot remove articles from the highlight")
        void authenticated_users_that_are_not_admins_cannot_remove_articles_from_the_highlight() {
            assertThrows(Exception.class, () -> mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("admins can remove articles from the highlight")
        void admins_can_remove_articles_from_the_highlight() {
            assertDoesNotThrow(() -> mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID)));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a not found status when the article does not exist")
        void should_return_a_not_found_status_when_the_article_does_not_exist() throws Exception {
            doThrow(ArticleNotFoundException.class).when(updater).removeArticleFromHighlights(ARTICLE_ID);

            mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a bad request status when the article was not highlighted")
        void should_return_a_bad_request_status_when_the_article_was_not_highlighted() throws Exception {
            doThrow(ArticleWasNotHighlightedException.class).when(updater).removeArticleFromHighlights(ARTICLE_ID);

            mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(delete("/api/articles/highlight/{id}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).removeArticleFromHighlights(ARTICLE_ID);
        }
    }

    @Nested
    @DisplayName("When updating the article highlights")
    class UpdatingArticleHighlightsIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot update articles highlights")
        void un_authenticated_users_cannot_update_articles_highlights() {
            assertThrows(Exception.class, () ->   mockMvc.perform(put("/api/articles/highlight")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loadResource("article/api/update_highlights_request.json"))));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot update articles highlights")
        void authenticated_users_that_are_not_admins_cannot_update_articles_highlights() {
            assertThrows(Exception.class, () ->   mockMvc.perform(put("/api/articles/highlight")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loadResource("article/api/update_highlights_request.json"))));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("admins can update articles highlights")
        void admins_can_update_articles_highlights() {
            assertDoesNotThrow(() ->   mockMvc.perform(put("/api/articles/highlight")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loadResource("article/api/update_highlights_request.json"))));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(put("/api/articles/highlight")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/update_highlights_request.json")))
                    .andExpect(status().is2xxSuccessful());
            verify(updater).updateArticlesHighlights(List.of(HighlightedArticle.builder().articleId("id").highlightRank(4).build()));
        }
    }

    @Nested
    @DisplayName("When fetching the list of highlighted articles")
    class FetchingHighlightedArticlesIntegrationTest {

        @Test
        @DisplayName("should return the list of articles on the happy path")
        void should_return_the_list_of_articles_on_the_happy_path() throws Exception {
            when(fetcher.getHighlightedArticles()).thenReturn(List.of(getArticle()));
            String content = mockMvc.perform(get("/api/articles/highlight"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/highlighted_articles_details_response.json"), content);
        }
    }

    @Nested
    @DisplayName("When fetching the summary of the highlighted articles")
    class FetchingHighlightedArticlesSummaryIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot fetch articles highlights summary")
        void un_authenticated_users_cannot_fetch_articles_highlights_summary() {
            assertThrows(Exception.class, () -> mockMvc.perform(get("/api/articles/highlight/summary")));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot fetch articles highlights summary")
        void authenticated_users_that_are_not_admins_cannot_fetch_articles_highlights_summary() {
            assertThrows(Exception.class, () -> mockMvc.perform(get("/api/articles/highlight/summary")));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("admins can fetch articles highlights summary")
        void admins_can_fetch_articles_highlights_summary() {
            assertDoesNotThrow(() -> mockMvc.perform(get("/api/articles/highlight/summary")));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return the list of articles summary on the happy path")
        void should_return_the_list_of_articles_on_the_happy_path() throws Exception {
            when(fetcher.getHighlightedArticlesSummary()).thenReturn(List.of(HighlightedArticle.builder().articleId(ARTICLE_ID).highlightRank(1).build()));
            String content = mockMvc.perform(get("/api/articles/highlight/summary"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/highlighted_articles_summary_response.json"), content);
        }
    }
}