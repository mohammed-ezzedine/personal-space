package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    public static final String ID = UUID.randomUUID().toString();
    public static final String NAME = UUID.randomUUID().toString();
    private CategoryStorage storage;
    private CategoryService service;
    private CategoryNameValidator nameValidator;
    private CategoryIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        storage = mock(CategoryStorage.class);
        nameValidator = mock(CategoryNameValidator.class);
        idGenerator = mock(CategoryIdGenerator.class);
        service = new CategoryService(storage, nameValidator, idGenerator);
    }

    @Nested
    @DisplayName("When fetching the list of existing categories")
    class FetchingAllCategoriesTest {

        @Test
        @DisplayName("it should fetch the categories from the storage")
        void it_should_fetch_the_categories_from_the_storage() {
            Category category = mock(Category.class);
            when(storage.fetchAll()).thenReturn(List.of(category));
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
        @DisplayName("should persist the new category with the option to be deleted in the future")
        void should_persist_the_new_category_with_the_option_to_be_deleted_in_the_future() throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
            service.persist(PersistCategoryRequest.builder().name(NAME).build());
            verify(storage).persist(Category.builder().id(ID).name(NAME).canBeDeleted(true).build());
        }

        @Test
        @DisplayName("should return the newly generated category id")
        void should_return_the_newly_generated_category_id() throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
            CategoryCreationResult creationResult = service.persist(PersistCategoryRequest.builder().name(NAME).build());
            assertEquals(ID, creationResult.getId());
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
        @DisplayName("it should fail if the category is marked not to be deleted")
        void it_should_fail_if_the_category_is_marked_not_to_be_deleted() {
            Category category = Category.builder().id(ID).name(NAME).canBeDeleted(false).build();
            when(storage.fetch(ID)).thenReturn(Optional.of(category));
            assertThrows(CannotDeleteCategoryException.class, () -> service.delete(ID));
        }

        @Test
        @DisplayName("it should delete the category from the store when found")
        void it_should_delete_the_category_from_the_store_when_found() throws CategoryNotFoundException, CannotDeleteCategoryException {
            Category category = Category.builder().id(ID).name(NAME).canBeDeleted(true).build();
            when(storage.fetch(ID)).thenReturn(Optional.of(category));
            service.delete(ID);
            verify(storage).delete(ID);
        }
    }
}