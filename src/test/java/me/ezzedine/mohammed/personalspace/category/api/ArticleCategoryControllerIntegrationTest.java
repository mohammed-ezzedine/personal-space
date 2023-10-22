package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.TestUtils;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategory;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        ArticleCategoryController.class
})
@EnableWebMvc
@AutoConfigureMockMvc
class ArticleCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleCategoryFetcher fetcher;

    @Nested
    @DisplayName("When fetching the summary of the existing categories")
    class FetchingCategorySummariesIntegrationTest {

        @BeforeEach
        void setUp() {
            ArticleCategory articleCategory = ArticleCategory.builder().id("articleCategoryId").name("articleCategoryName").canBeDeleted(true).build();
            when(fetcher.fetchAll()).thenReturn(List.of(articleCategory));
        }

        @Test
        @DisplayName("it should return a success status")
        void it_should_return_a_success_status() throws Exception {
            mockMvc.perform(get("/api/categories")).andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("it should return the data with the correct format")
        void it_should_return_the_data_with_the_correct_format() throws Exception {
            String response = mockMvc.perform(get("/api/categories")).andReturn().getResponse().getContentAsString();
            String resource = TestUtils.getResource("category/api/category_summary_list.json");
            assertEquals(resource, response);
        }

    }
}