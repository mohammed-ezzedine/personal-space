package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryIdAlreadyExistsAdvice;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryValidationViolationAdvice;
import me.ezzedine.mohammed.personalspace.category.core.*;
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
import static me.ezzedine.mohammed.personalspace.TestUtils.loadResourceWithWhiteSpaces;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        CategoryController.class,
        CategoryValidationViolationAdvice.class,
        CategoryIdAlreadyExistsAdvice.class
})
@EnableWebMvc
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryFetcher fetcher;

    @MockBean
    private CategoryPersister persister;

    @Nested
    @DisplayName("When fetching the summary of the existing categories")
    class FetchingCategorySummariesIntegrationTest {

        @BeforeEach
        void setUp() {
            Category category = Category.builder().id("articleCategoryId").name("articleCategoryName").canBeDeleted(true).build();
            when(fetcher.fetchAll()).thenReturn(List.of(category));
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
            String resource = loadResource("category/api/category_summary_list.json");
            assertEquals(resource, response);
        }

    }

    @Nested
    @DisplayName("When creating a new category")
    class CreatingNewCategoryIntegrationTest {

        @BeforeEach
        void setUp() throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
            when(persister.persist(any())).thenReturn(CategoryCreationResult.builder().id("categoryId").build());
        }

        @Test
        @DisplayName("it should return a created status code on success")
        void it_should_return_a_created_status_code_on_success() throws Exception {
            mockMvc.perform(post("/api/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("it should return the created category id in the response")
        void it_should_return_the_created_category_id_in_the_response() throws Exception {
            String response = mockMvc
                    .perform(post("/api/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResource("category/api/category_creation_response.json");
            assertEquals(expectedResponse, response);
        }

        @Test
        @DisplayName("should return a bad request with the reasons of failure when the name is not valid")
        void should_return_a_bad_request_with_the_reasons_of_failure_when_the_name_is_not_valid() throws Exception {
            when(persister.persist(any())).thenThrow(new CategoryValidationViolationException(List.of("firstReason", "secondReason")));

            String response = mockMvc.perform(post("/api/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResource("category/api/category_creation_validation_failure_response.json");
            assertEquals(expectedResponse, response);
        }

        @Test
        @DisplayName("should return a conflict status code when another category with a similar id already exists")
        void should_return_a_conflict_status_code_when_another_category_with_a_similar_id_already_exists() throws Exception {
            when(persister.persist(any())).thenThrow(new CategoryIdAlreadyExistsException("categoryName"));

            String response = mockMvc.perform(post("/api/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResourceWithWhiteSpaces("category/api/category_creation_id_conflict_response.json");
            assertEquals(expectedResponse, response);
        }
    }
}