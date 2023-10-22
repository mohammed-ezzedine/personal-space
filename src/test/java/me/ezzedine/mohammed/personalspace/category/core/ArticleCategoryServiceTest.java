package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleCategoryServiceTest {

    public static final String ID = UUID.randomUUID().toString();
    public static final String NAME = UUID.randomUUID().toString();
    private ArticleCategoryStorage storage;
    private ArticleCategoryService service;
    private ArticleCategoryNameValidator nameValidator;
    private ArticleCategoryIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        storage = mock(ArticleCategoryStorage.class);
        nameValidator = mock(ArticleCategoryNameValidator.class);
        idGenerator = mock(ArticleCategoryIdGenerator.class);
        service = new ArticleCategoryService(storage, nameValidator, idGenerator);
    }

    @Nested
    @DisplayName("When fetching the list of existing categories")
    class FetchingAllCategoriesTest {

        @Test
        @DisplayName("it should fetch the categories from the storage")
        void it_should_fetch_the_categories_from_the_storage() {
            ArticleCategory category = mock(ArticleCategory.class);
            when(storage.fetchAll()).thenReturn(List.of(category));
            List<ArticleCategory> articleCategories = service.fetchAll();
            assertEquals(1, articleCategories.size());
            assertEquals(category, articleCategories.get(0));
        }
    }

    @Nested
    @DisplayName("When persisting a new article category")
    class PersistingArticleCategoryTest {

        @BeforeEach
        void setup() {
            when(nameValidator.validate(NAME)).thenReturn(ArticleCategoryNameValidationResult.builder().valid(true).build());
            when(idGenerator.generate(NAME)).thenReturn(ID);
        }

        @Test
        @DisplayName("it should fail if the name is not valid")
        void it_should_fail_if_the_name_is_not_valid() {
            ArticleCategoryNameValidationResult validationResult = ArticleCategoryNameValidationResult.builder().valid(false)
                    .violations(List.of(ArticleCategoryNameViolation.NO_DIGITS, ArticleCategoryNameViolation.NO_SPECIAL_CHARACTERS)).build();
            when(nameValidator.validate(NAME)).thenReturn(validationResult);

            PersistArticleCategoryRequest request = PersistArticleCategoryRequest.builder().name(NAME).build();
            ArticleCategoryValidationViolationException exception = assertThrows(ArticleCategoryValidationViolationException.class, () -> service.persist(request));
            assertEquals(2, exception.getReasons().size());
            assertTrue(exception.getReasons().contains(ArticleCategoryNameViolation.NO_DIGITS.getMessage()));
            assertTrue(exception.getReasons().contains(ArticleCategoryNameViolation.NO_SPECIAL_CHARACTERS.getMessage()));
        }

        @Test
        @DisplayName("it should fail if a category with the same id already exists")
        void it_should_fail_if_a_category_with_the_same_id_already_exists() {
            when(storage.categoryExists(ID)).thenReturn(true);
            assertThrows(ArticleCategoryIdAlreadyExistsException.class, () -> service.persist(PersistArticleCategoryRequest.builder().name(NAME).build()));
        }

        @Test
        @DisplayName("should persist the new category with the option to be deleted in the future")
        void should_persist_the_new_category_with_the_option_to_be_deleted_in_the_future() throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException {
            service.persist(PersistArticleCategoryRequest.builder().name(NAME).build());
            verify(storage).persist(ArticleCategory.builder().id(ID).name(NAME).canBeDeleted(true).build());
        }

        @Test
        @DisplayName("should return the newly generated category id")
        void should_return_the_newly_generated_category_id() throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException {
            ArticleCategoryCreationResult creationResult = service.persist(PersistArticleCategoryRequest.builder().name(NAME).build());
            assertEquals(ID, creationResult.getId());
        }
    }

    @Nested
    @DisplayName("When deleting an article category")
    class DeletingArticleCategoryTest {

        @Test
        @DisplayName("it should fail if the id does not exist")
        void it_should_fail_if_the_id_does_not_exist() {
            when(storage.categoryExists(ID)).thenReturn(false);
            assertThrows(ArticleCategoryNotFoundException.class, () -> service.delete(ID));
        }

        @Test
        @DisplayName("it should delete the category from the store when found")
        void it_should_delete_the_category_from_the_store_when_found() throws ArticleCategoryNotFoundException {
            when(storage.categoryExists(ID)).thenReturn(true);
            service.delete(ID);
            verify(storage).delete(ID);
        }
    }
}