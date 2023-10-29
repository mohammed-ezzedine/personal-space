package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.api.advice.ArticleNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.article.core.*;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
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

import static me.ezzedine.mohammed.personalspace.TestUtils.loadResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        ArticleController.class,
        CategoryNotFoundAdvice.class,
        ArticleNotFoundAdvice.class
})
@EnableWebMvc
@AutoConfigureMockMvc
class ArticleControllerIntegrationTest {

    public static final String ARTICLE_ID = "articleId";
    public static final String TITLE = "articleTitle";
    public static final String DESCRIPTION = "articleDescription";
    public static final String CONTENT = "articleContent";
    public static final String CATEGORY_ID = "articleCategoryId";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleCreator articleCreator;

    @MockBean
    private ArticleFetcher articleFetcher;

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
                    post("/api/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andExpect(status().isCreated());

            ArticleCreationRequest request = ArticleCreationRequest.builder().title(TITLE).description(DESCRIPTION)
                    .content(CONTENT).categoryId(CATEGORY_ID).build();
            verify(articleCreator).create(request);
        }

        @Test
        @DisplayName("should return a not found status code when the category id does not exist")
        void should_return_a_not_found_status_code_when_the_category_id_does_not_exist() throws Exception {
            when(articleCreator.create(any())).thenThrow(CategoryNotFoundException.class);
            mockMvc.perform(
                    post("/api/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return the newly created article id on the happy path")
        void should_return_the_newly_created_article_id_on_the_happy_path() throws Exception {
            String response = mockMvc.perform(
                    post("/api/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("article/api/create_article_request.json"))
            ).andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/article_creation_response.json"), response);
        }
    }

    @Nested
    @DisplayName("When fetching the details of an article")
    class FetchingArticleDetailsIntegrationTest {

        @Test
        @DisplayName("should return a not found status code when the article id does not exist")
        void should_return_a_not_found_status_code_when_the_article_id_does_not_exist() throws Exception {
            when(articleFetcher.fetch(ARTICLE_ID)).thenThrow(ArticleNotFoundException.class);

            mockMvc.perform(get("/api/articles/{articleId}", ARTICLE_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return a success status code with the details when the article exists")
        void should_return_a_success_status_code_with_the_details_when_the_article_exists() throws Exception {
            when(articleFetcher.fetch(ARTICLE_ID)).thenReturn(getArticle());

            String response = mockMvc.perform(get("/api/articles/{articleId}", ARTICLE_ID))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/article_details_response.json"), response);
        }
    }

    private Article getArticle() {
        return Article.builder().id(ARTICLE_ID).title(TITLE).description(DESCRIPTION).content(CONTENT).categoryId(CATEGORY_ID).build();
    }
}