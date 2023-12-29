package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.SecurityTestConfiguration;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryDeletionRejectedAdvice;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryIdAlreadyExistsAdvice;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryNotFoundAdvice;
import me.ezzedine.mohammed.personalspace.category.api.advice.CategoryValidationViolationAdvice;
import me.ezzedine.mohammed.personalspace.category.core.*;
import me.ezzedine.mohammed.personalspace.category.core.UpdateCategoriesOrdersRequest.CategoryOrder;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static me.ezzedine.mohammed.personalspace.TestUtils.loadResource;
import static me.ezzedine.mohammed.personalspace.TestUtils.loadResourceWithWhiteSpaces;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        CategoryController.class,
        CategoryValidationViolationAdvice.class,
        CategoryIdAlreadyExistsAdvice.class,
        CategoryNotFoundAdvice.class,
        CategoryDeletionRejectedAdvice.class
})
@Import(SecurityTestConfiguration.class)
@EnableWebMvc
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryFetcher fetcher;

    @MockBean
    private CategoryPersister persister;

    @MockBean
    private CategoryDeleter deleter;

    @Nested
    @DisplayName("When fetching the summary of the existing categories")
    class FetchingCategorySummariesIntegrationTest {

        @BeforeEach
        void setUp() {
            Category category = Category.builder().id("articleCategoryId").name("articleCategoryName").order(7).build();
            when(fetcher.fetchAll()).thenReturn(List.of(category));
        }

        @Test
        @DisplayName("it should return a success status")
        void it_should_return_a_success_status() throws Exception {
            mockMvc.perform(get("/categories")).andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("it should return the data with the correct format")
        void it_should_return_the_data_with_the_correct_format() throws Exception {
            String response = mockMvc.perform(get("/categories")).andReturn().getResponse().getContentAsString();
            String resource = loadResource("category/api/category_summary_list.json");
            assertEquals(resource, response);
        }
    }

    @Nested
    @DisplayName("When fetching the details of a category")
    class FetchingCategoryDetailsIntegrationTest {
        @Test
        @DisplayName("the user should get the category details on the happy path")
        void the_user_should_get_the_category_details_on_the_happy_path() throws Exception {
            when(fetcher.fetch("categoryId")).thenReturn(Category.builder().id("categoryId").name("categoryName").order(3).build());
            String response = mockMvc.perform(get("/categories/categoryId"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();
            String resource = loadResource("category/api/category_details_response.json");
            assertEquals(resource, response);
        }

        @Test
        @DisplayName("the user should get a not found status code when the category does not exist")
        void the_user_should_get_a_not_found_status_code_when_the_category_does_not_exist() throws Exception {
            when(fetcher.fetch(anyString())).thenThrow(CategoryNotFoundException.class);
            mockMvc.perform(get("/categories/categoryId"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("When creating a new category")
    class CreatingNewCategoryIntegrationTest {

        @BeforeEach
        void setUp() throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
            when(persister.persist(any())).thenReturn(CategoryCreationResult.builder().id("categoryId").order(4).build());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot create categories")
        void un_authenticated_users_cannot_create_categories() {
            assertThrows(Exception.class, () -> mockMvc.perform(
                    post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON)
            ));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot create categories")
        void authenticated_users_that_are_not_admins_cannot_create_categories() {
            assertThrows(Exception.class, () -> mockMvc.perform(
                    post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON)
            ));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("only admins can create categories")
        void only_admins_can_create_categories() {
            assertDoesNotThrow(() -> mockMvc.perform(
                    post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON)
            ));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("it should return a created status code on success")
        void it_should_return_a_created_status_code_on_success() throws Exception {
            mockMvc.perform(post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("it should return the created category id and order in the response")
        void it_should_return_the_created_category_id_and_order_in_the_response() throws Exception {
            String response = mockMvc
                    .perform(post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResource("category/api/category_creation_response.json");
            assertEquals(expectedResponse, response);
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a bad request with the reasons of failure when the name is not valid")
        void should_return_a_bad_request_with_the_reasons_of_failure_when_the_name_is_not_valid() throws Exception {
            when(persister.persist(any())).thenThrow(new CategoryValidationViolationException(List.of("firstReason", "secondReason")));

            String response = mockMvc.perform(post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResource("category/api/category_creation_validation_failure_response.json");
            assertEquals(expectedResponse, response);
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a conflict status code when another category with a similar id already exists")
        void should_return_a_conflict_status_code_when_another_category_with_a_similar_id_already_exists() throws Exception {
            when(persister.persist(any())).thenThrow(new CategoryIdAlreadyExistsException("categoryName"));

            String response = mockMvc.perform(post("/categories")
                            .content(loadResource("category/api/create_category_request.json"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = loadResourceWithWhiteSpaces("category/api/category_creation_id_conflict_response.json");
            assertEquals(expectedResponse, response);
        }
    }

    @Nested
    @DisplayName("When updating the categories orders")
    class UpdatingCategoriesOrdersIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot update categories order")
        void un_authenticated_users_cannot_update_categories_order() {
            assertThrows(Exception.class, () -> mockMvc.perform(
                    put("/categories/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("category/api/update_orders_request.json"))
            ));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot update categories order")
        void authenticated_users_that_are_not_admins_cannot_update_categories_order() {
            assertThrows(Exception.class, () -> mockMvc.perform(
                    put("/categories/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("category/api/update_orders_request.json"))
            ));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("only admins can update categories order")
        void only_admins_can_update_categories_order() {
            assertDoesNotThrow(() -> mockMvc.perform(
                    put("/categories/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("category/api/update_orders_request.json")
            )));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a success status upon successfully updating the orders")
        void should_return_a_success_status_upon_successfully_updating_the_orders() throws Exception {
            mockMvc.perform(
                    put("/categories/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loadResource("category/api/update_orders_request.json"))
            ).andExpect(status().is2xxSuccessful());

            UpdateCategoriesOrdersRequest request = UpdateCategoriesOrdersRequest.builder()
                    .categoryOrders(List.of(CategoryOrder.builder().categoryId("categoryId").order(4).build())).build();
            verify(persister).updateCategoriesOrders(request);
        }
    }

    @Nested
    @DisplayName("When deleting a category")
    class DeletingCategoryIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("un authenticated users cannot delete categories")
        void un_authenticated_users_cannot_delete_categories() {
            assertThrows(Exception.class, () -> mockMvc.perform(delete("/categories/categoryId")));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot delete categories")
        void authenticated_users_that_are_not_admins_cannot_delete_categories() {
            assertThrows(Exception.class, () -> mockMvc.perform(delete("/categories/categoryId")));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("only admins can delete categories")
        void only_admins_can_delete_categories() {
            assertDoesNotThrow(() -> mockMvc.perform(delete("/categories/categoryId")));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a not found exception if the category does not exist")
        void should_return_a_not_found_exception_if_the_category_does_not_exist() throws Exception {
            doThrow(CategoryNotFoundException.class).when(deleter).delete("categoryId");
            mockMvc.perform(delete("/categories/categoryId"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a bad request when the category cannot be deleted")
        void should_return_a_bad_request_when_the_category_cannot_be_deleted() throws Exception {
            String rejectionReason = UUID.randomUUID().toString();
            doThrow(new CategoryDeletionRejectedException(rejectionReason)).when(deleter).delete("categoryId");
            String response = mockMvc.perform(delete("/categories/categoryId"))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            assertEquals(rejectionReason, response);
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a success status when the category is successfully deleted")
        void should_return_a_success_status_when_the_category_is_successfully_deleted() throws Exception {
            mockMvc.perform(delete("/categories/categoryId"))
                    .andExpect(status().is2xxSuccessful());
        }
    }
}