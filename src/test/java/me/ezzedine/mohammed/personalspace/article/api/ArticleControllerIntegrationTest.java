package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.api.advice.ArticleNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.article.core.*;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import me.ezzedine.mohammed.personalspace.util.pagination.PaginationCriteria;
import me.ezzedine.mohammed.personalspace.util.sort.SortingCriteria;
import org.junit.jupiter.api.BeforeEach;
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
import static me.ezzedine.mohammed.personalspace.article.api.ArticleApiTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        ArticleController.class,
        CategoryNotFoundAdvice.class,
        ArticleNotFoundAdvice.class
})
@EnableWebMvc
@AutoConfigureMockMvc
class ArticleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleCreator articleCreator;

    @MockBean
    private ArticleFetcher articleFetcher;

    @MockBean
    private ArticleEditor articleEditor;

    @Nested
    @DisplayName("When fetching the details of all articles")
    class FetchingAllArticlesDetailsIntegrationTest {

        @Nested
        @DisplayName("When specifying pagination criteria")
        class FetchingPaginatedArticleDetailsIntegrationTest {

            @Test
            @DisplayName("should return a success status code with the details of the articles")
            void should_return_a_success_status_code_with_the_details_of_the_articles() throws Exception {
                Page<Article> page = Page.<Article>builder().totalSize(14).items(List.of(getArticle())).build();
                PaginationCriteria paginationCriteria = PaginationCriteria.builder().maximumPageSize(13).startingPageIndex(2).build();
                when(articleFetcher.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build())).thenReturn(page);

                String response = mockMvc.perform(get("/articles")
                                .param("page", "2")
                                .param("size", "13"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

                assertEquals(loadResource("article/api/all_articles_details_response.json"), response);
            }

            @Test
            @DisplayName("should default the page size to 10 articles if the user specified the page index but not its size")
            void should_default_the_page_size_to_10_articles_if_the_user_specified_the_page_index_but_not_its_size() throws Exception {
                Page<Article> page = Page.<Article>builder().totalSize(14).items(List.of(getArticle())).build();
                PaginationCriteria paginationCriteria = PaginationCriteria.builder().maximumPageSize(10).startingPageIndex(2).build();
                when(articleFetcher.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build())).thenReturn(page);

                String response = mockMvc.perform(get("/articles")
                                .param("page", "2"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

                assertEquals(loadResource("article/api/all_articles_details_response.json"), response);
            }
        }

        @Nested
        @DisplayName("When specifying a filtering criteria on the highlight property")
        class FetchingHighlightFilteredArticleDetailsIntegrationTest {
            @Test
            @DisplayName("should return a success status code with the details of the articles")
            void should_return_a_success_status_code_with_the_details_of_the_articles() throws Exception {
                Page<Article> page = Page.<Article>builder().totalSize(14).items(List.of(getArticle())).build();
                when(articleFetcher.fetchAll(ArticlesFetchCriteria.builder().highlighted(true).build())).thenReturn(page);

                String response = mockMvc.perform(get("/articles")
                                .param("highlighted", "true"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

                assertEquals(loadResource("article/api/all_articles_details_response.json"), response);
            }
        }

        @Nested
        @DisplayName("When sorting by a specific field")
        class FetchSortedArticlesDetailsIntegrationTest {

            @Test
            @DisplayName("should return a success status code with the details of the articles")
            void should_return_a_success_status_code_with_the_details_of_the_articles() throws Exception {
                Page<Article> page = Page.<Article>builder().totalSize(14).items(List.of(getArticle())).build();
                SortingCriteria sortingCriteria = SortingCriteria.builder().field("createdDate").ascendingOrder(false).build();
                when(articleFetcher.fetchAll(ArticlesFetchCriteria.builder().sortingCriteria(sortingCriteria).build())).thenReturn(page);

                String response = mockMvc.perform(get("/articles")
                                .param("sortBy", "createdDate")
                                .param("ascOrder", "false"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

                assertEquals(loadResource("article/api/all_articles_details_response.json"), response);
            }

            @Test
            @DisplayName("should default to the ascending order sorting when not mentioned")
            void should_default_to_the_ascending_order_sorting_when_not_mentioned() throws Exception {
                Page<Article> page = Page.<Article>builder().totalSize(14).items(List.of(getArticle())).build();
                SortingCriteria sortingCriteria = SortingCriteria.builder().field("createdDate").ascendingOrder(true).build();
                when(articleFetcher.fetchAll(ArticlesFetchCriteria.builder().sortingCriteria(sortingCriteria).build())).thenReturn(page);

                String response = mockMvc.perform(get("/articles")
                                .param("sortBy", "createdDate"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getResponse().getContentAsString();

                assertEquals(loadResource("article/api/all_articles_details_response.json"), response);
            }
        }
    }

    @Nested
    @DisplayName("When fetching the details of an article")
    class FetchingArticleDetailsIntegrationTest {

        @Test
        @DisplayName("should return a not found status code when the article id does not exist")
        void should_return_a_not_found_status_code_when_the_article_id_does_not_exist() throws Exception {
            when(articleFetcher.fetch(ARTICLE_ID)).thenThrow(ArticleNotFoundException.class);

            mockMvc.perform(get("/articles/{articleId}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return a success status code with the details when the article exists")
        void should_return_a_success_status_code_with_the_details_when_the_article_exists() throws Exception {
            when(articleFetcher.fetch(ARTICLE_ID)).thenReturn(getArticle());

            String response = mockMvc.perform(get("/articles/{articleId}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/article_details_response.json"), response);
        }
    }

    @Nested
    @DisplayName("When creating a new category")
    class CreatingNewCategoryIntegrationTest {

        @BeforeEach
        void setUp() throws CategoryNotFoundException {
            when(articleCreator.create(any())).thenReturn(ArticleCreationResult.builder().id(ARTICLE_ID).build());
        }

        @Test
        @DisplayName("should return a created success status on the happy path")
        void should_return_a_created_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(
                    post("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andExpect(status().isCreated());

            ArticleCreationRequest request = ArticleCreationRequest.builder().title(TITLE).description(DESCRIPTION)
                    .content(CONTENT).categoryId(CATEGORY_ID).thumbnailImageUrl(ARTICLE_THUMBNAIL_IMAGE_URL)
                    .keywords(List.of(KEYWORD)).hidden(HIDDEN).build();
            verify(articleCreator).create(request);
        }

        @Test
        @DisplayName("should return a not found status code when the category id does not exist")
        void should_return_a_not_found_status_code_when_the_category_id_does_not_exist() throws Exception {
            when(articleCreator.create(any())).thenThrow(CategoryNotFoundException.class);
            mockMvc.perform(
                    post("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return the newly created article id on the happy path")
        void should_return_the_newly_created_article_id_on_the_happy_path() throws Exception {
            String response = mockMvc.perform(
                    post("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/article_creation_response.json"), response);
        }
    }

    @Nested
    @DisplayName("When editing an article")
    class EditingArticleIntegrationTest {
        @Test
        @DisplayName("should return a success status on the happy path")
        void should_return_a_success_status_on_the_happy_path() throws Exception {
            mockMvc.perform(
                    put("/articles/{id}", ARTICLE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/edit_article_request.json"))
            ).andExpect(status().is2xxSuccessful());

            ArticleUpdateRequest request = ArticleUpdateRequest.builder().id(ARTICLE_ID).title("updatedArticleTitle")
                    .categoryId("updatedArticleCategoryId").content("updatedArticleContent").description("updatedArticleDescription")
                    .thumbnailImageUrl("updatedArticleThumbnailImageUrl").keywords(List.of("updatedArticleKeyword"))
                    .hidden(true).build();
            verify(articleEditor).edit(request);
        }

        @Test
        @DisplayName("should return a not found status when the article does not exist")
        void should_return_a_not_found_status_when_the_article_does_not_exist() throws Exception {
            doThrow(ArticleNotFoundException.class).when(articleEditor).edit(any());

            mockMvc.perform(
                    put("/articles/{id}", ARTICLE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/edit_article_request.json"))
            ).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return a not found status when the chosen category does not exist")
        void should_return_a_not_found_status_when_the_chosen_category_does_not_exist() throws Exception {
            doThrow(CategoryNotFoundException.class).when(articleEditor).edit(any());

            mockMvc.perform(
                    put("/articles/{id}", ARTICLE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/edit_article_request.json"))
            ).andExpect(status().isNotFound());
        }
    }
}