package me.ezzedine.mohammed.personalspace.category.core;

import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermission;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermissionGranter;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;
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
    private CategoryDeletionPermissionGranter firstDeletionPermissionGranter;
    private CategoryDeletionPermissionGranter secondDeletionPermissionGranter;

    @BeforeEach
    void setUp() {
        storage = mock(CategoryStorage.class);
        nameValidator = mock(CategoryNameValidator.class);
        idGenerator = mock(CategoryIdGenerator.class);
        orderResolver = mock(CategoryOrderResolver.class);
        firstDeletionPermissionGranter = mock(CategoryDeletionPermissionGranter.class);
        secondDeletionPermissionGranter = mock(CategoryDeletionPermissionGranter.class);
        service = new CategoryService(storage, nameValidator, idGenerator, orderResolver, List.of(firstDeletionPermissionGranter, secondDeletionPermissionGranter));
    }

    @Nested
    @DisplayName("When fetching a category by id")
    class FetchingCategoryTest {

        @Test
        @DisplayName("should retrieve it from the storage")
        void should_retrieve_it_from_the_storage() throws CategoryNotFoundException {
            Category category = mock(Category.class);
            when(storage.fetch(ID)).thenReturn(Optional.of(category));
            assertEquals(category, service.fetch(ID));
        }

        @Test
        @DisplayName("should fail if the category does not exist")
        void should_fail_if_the_category_does_not_exist() {
            when(storage.fetch(ID)).thenReturn(Optional.empty());
            assertThrows(CategoryNotFoundException.class, () -> service.fetch(ID));
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

        @BeforeEach
        void setUp() {
            when(storage.fetch(ID)).thenReturn(Optional.of(getCategory()));
        }

        @Test
        @DisplayName("it should fail if the id does not exist")
        void it_should_fail_if_the_id_does_not_exist() {
            when(storage.fetch(ID)).thenReturn(Optional.empty());
            assertThrows(CategoryNotFoundException.class, () -> service.delete(ID));
        }

        @Test
        @DisplayName("the user should not be allowed to delete a category if it is not allowed to get deleted")
        void the_user_should_not_be_allowed_to_delete_a_category_if_it_is_not_allowed_to_get_deleted() {
            String reason = UUID.randomUUID().toString();
            when(firstDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.allowed());
            when(secondDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.notAllowed(reason));

            CategoryDeletionRejectedException exception = assertThrows(CategoryDeletionRejectedException.class, () -> service.delete(ID));
            assertEquals(reason, exception.getMessage());
        }

        @Test
        @DisplayName("the user should receive a message when the deletion is rejected and the reason cannot be deduced")
        void the_user_should_receive_a_message_when_the_deletion_is_rejected_and_the_reason_cannot_be_deduced() {
            when(firstDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.allowed());
            when(secondDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.notAllowed(null));
            CategoryDeletionRejectedException exception = assertThrows(CategoryDeletionRejectedException.class, () -> service.delete(ID));
            assertEquals("Category cannot be deleted at the moment.", exception.getMessage());
        }

        @Test
        @DisplayName("the user should be able to delete an existing category if the permission is granted")
        void the_user_should_be_able_to_delete_an_existing_category_if_the_permission_is_granted() throws CategoryNotFoundException, CategoryDeletionRejectedException {
            when(firstDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.allowed());
            when(secondDeletionPermissionGranter.canDeleteCategory(ID)).thenReturn(CategoryDeletionPermission.allowed());
            Category category = Category.builder().id(ID).name(NAME).build();
            when(storage.fetch(ID)).thenReturn(Optional.of(category));
            service.delete(ID);
            verify(storage).delete(ID);
        }

    }

    private static Category getCategory() {
        return Category.builder().id(ID).name(NAME).build();
    }
}