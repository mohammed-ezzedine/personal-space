package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private static final String ID = UUID.randomUUID().toString();
    private static final String NAME = UUID.randomUUID().toString();
    private static final int ORDER = new Random().nextInt();
    private CategoryStorage storage;
    private CategoryService service;
    private CategoryNameValidator nameValidator;
    private CategoryIdGenerator idGenerator;
    private CategoryOrderResolver orderResolver;

    @BeforeEach
    void setUp() {
        storage = mock(CategoryStorage.class);
        nameValidator = mock(CategoryNameValidator.class);
        idGenerator = mock(CategoryIdGenerator.class);
        orderResolver = mock(CategoryOrderResolver.class);
        service = new CategoryService(storage, nameValidator, idGenerator, orderResolver);
    }

    @Nested
    @DisplayName("When checking if a category exists by id")
    class CheckingCategoryExistsTest {

        @Test
        @DisplayName("should consult the storage")
        void should_consult_the_storage() {
            String id = UUID.randomUUID().toString();
            boolean verdict = new Random().nextBoolean();
            when(storage.categoryExists(id)).thenReturn(verdict);
            boolean exists = service.exists(id);
            assertEquals(verdict, exists);
        }
    }

    @Nested
    @DisplayName("When fetching the list of existing categories")
    class FetchingAllCategoriesTest {

        @Test
        @DisplayName("it should fetch the categories from the storage")
        void it_should_fetch_the_categories_from_the_storage() {
            Category category = mock(Category.class);
            when(storage.fetchAllOrderedByOrder()).thenReturn(List.of(category));
            List<Category> articleCategories = service.fetchAll();
            assertEquals(1, articleCategories.size());
            assertEquals(category, articleCategories.get(0));
        }
    }

    @Nested
    @DisplayName("When persisting a new article category")
    class PersistingCategoryTest {

        @BeforeEach
        void setup() {
            when(nameValidator.validate(NAME)).thenReturn(CategoryNameValidationResult.builder().valid(true).build());
            when(idGenerator.generate(NAME)).thenReturn(ID);
            when(orderResolver.resolveOrderForNewCategory()).thenReturn(ORDER);
        }

        @Test
        @DisplayName("it should fail if the name is not valid")
        void it_should_fail_if_the_name_is_not_valid() {
            CategoryNameValidationResult validationResult = CategoryNameValidationResult.builder().valid(false)
                    .violations(List.of(CategoryNameViolation.NO_DIGITS, CategoryNameViolation.NO_SPECIAL_CHARACTERS)).build();
            when(nameValidator.validate(NAME)).thenReturn(validationResult);

            PersistCategoryRequest request = PersistCategoryRequest.builder().name(NAME).build();
            CategoryValidationViolationException exception = assertThrows(CategoryValidationViolationException.class, () -> service.persist(request));
            assertEquals(2, exception.getReasons().size());
            assertTrue(exception.getReasons().contains(CategoryNameViolation.NO_DIGITS.getMessage()));
            assertTrue(exception.getReasons().contains(CategoryNameViolation.NO_SPECIAL_CHARACTERS.getMessage()));
        }

        @Test
        @DisplayName("it should fail if a category with the same id already exists")
        void it_should_fail_if_a_category_with_the_same_id_already_exists() {
            when(storage.categoryExists(ID)).thenReturn(true);
            assertThrows(CategoryIdAlreadyExistsException.class, () -> service.persist(PersistCategoryRequest.builder().name(NAME).build()));
        }

        @Test
        @DisplayName("it should persist the new category with the resolved order")
        void it_should_persist_the_new_category_with_the_resolved_order() throws CategoryIdAlreadyExistsException, CategoryValidationViolationException {
            service.persist(PersistCategoryRequest.builder().name(NAME).build());

            ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);
            verify(storage).persist(argumentCaptor.capture());

            assertEquals(ORDER, argumentCaptor.getValue().getOrder());
        }

        @Test
        @DisplayName("should return the newly generated category id")
        void should_return_the_newly_generated_category_id() throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
            CategoryCreationResult creationResult = service.persist(PersistCategoryRequest.builder().name(NAME).build());
            assertEquals(ID, creationResult.getId());
        }

        @Test
        @DisplayName("should return the newly generated category order")
        void should_return_the_newly_generated_category_order() throws CategoryIdAlreadyExistsException, CategoryValidationViolationException {
            CategoryCreationResult creationResult = service.persist(PersistCategoryRequest.builder().name(NAME).build());
            assertEquals(ORDER, creationResult.getOrder());
        }
    }

    @Nested
    @DisplayName("When updating the order of the categories")
    class UpdatingCategoryOrdersTest {
        @Test
        @DisplayName("should not fail if some of the category ids did not exist")
        void should_not_fail_if_some_of_the_category_ids_did_not_exist() {
            String id = UUID.randomUUID().toString();
            when(storage.fetch(id)).thenReturn(Optional.empty());

            UpdateCategoriesOrdersRequest.CategoryOrder categoryOrder = getCategoryOrderRequest(id);
            UpdateCategoriesOrdersRequest request = getUpdateOrdersRequest(List.of(categoryOrder));
            assertDoesNotThrow(() -> service.updateCategoriesOrders(request));
        }

        @Test
        @DisplayName("should update the order for each of the specified categories")
        void should_update_the_order_for_each_of_the_specified_categories() {
            String firstCategoryId = UUID.randomUUID().toString();
            String secondCategoryId = UUID.randomUUID().toString();
            when(storage.fetch(firstCategoryId)).thenReturn(Optional.of(getCategory(firstCategoryId)));
            when(storage.fetch(secondCategoryId)).thenReturn(Optional.of(getCategory(secondCategoryId)));

            UpdateCategoriesOrdersRequest.CategoryOrder firstCategoryOrderRequest = getCategoryOrderRequest(firstCategoryId);
            UpdateCategoriesOrdersRequest.CategoryOrder secondCategoryOrderRequest = getCategoryOrderRequest(secondCategoryId);

            UpdateCategoriesOrdersRequest request = getUpdateOrdersRequest(List.of(firstCategoryOrderRequest, secondCategoryOrderRequest));
            service.updateCategoriesOrders(request);

            ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);
            verify(storage, times(2)).persist(argumentCaptor.capture());

            assertEquals(firstCategoryId, argumentCaptor.getAllValues().get(0).getId());
            assertEquals(firstCategoryOrderRequest.getOrder(), argumentCaptor.getAllValues().get(0).getOrder());

            assertEquals(secondCategoryId, argumentCaptor.getAllValues().get(1).getId());
            assertEquals(secondCategoryOrderRequest.getOrder(), argumentCaptor.getAllValues().get(1).getOrder());
        }

        @Test
        @DisplayName("should not modify the other properties of the updated categories")
        void should_not_modify_the_other_properties_of_the_updated_categories() {
            String id = UUID.randomUUID().toString();
            Category category = getCategory(id);
            when(storage.fetch(id)).thenReturn(Optional.of(category));

            UpdateCategoriesOrdersRequest.CategoryOrder categoryOrderRequest = getCategoryOrderRequest(id);
            UpdateCategoriesOrdersRequest request = getUpdateOrdersRequest(List.of(categoryOrderRequest));
            service.updateCategoriesOrders(request);

            ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);
            verify(storage).persist(argumentCaptor.capture());

            assertEquals(category.getName(), argumentCaptor.getValue().getName());
        }

        private static UpdateCategoriesOrdersRequest getUpdateOrdersRequest(List<UpdateCategoriesOrdersRequest.CategoryOrder> firstCategoryOrderRequest) {
            return UpdateCategoriesOrdersRequest.builder().categoryOrders(firstCategoryOrderRequest).build();
        }

        private static UpdateCategoriesOrdersRequest.CategoryOrder getCategoryOrderRequest(String categoryId) {
            return UpdateCategoriesOrdersRequest.CategoryOrder.builder().categoryId(categoryId).order(new Random().nextInt()).build();
        }

        private static Category getCategory(String id) {
            return Category.builder().id(id).name(UUID.randomUUID().toString()).build();
        }
    }

    @Nested
    @DisplayName("When deleting an article category")
    class DeletingCategoryTest {

        @Test
        @DisplayName("it should fail if the id does not exist")
        void it_should_fail_if_the_id_does_not_exist() {
            when(storage.fetch(ID)).thenReturn(Optional.empty());
            assertThrows(CategoryNotFoundException.class, () -> service.delete(ID));
        }

        @Test
        @DisplayName("it should delete the category from the store when found")
        void it_should_delete_the_category_from_the_store_when_found() throws CategoryNotFoundException {
            Category category = Category.builder().id(ID).name(NAME).build();
            when(storage.fetch(ID)).thenReturn(Optional.of(category));
            service.delete(ID);
            verify(storage).delete(ID);
        }
    }
}