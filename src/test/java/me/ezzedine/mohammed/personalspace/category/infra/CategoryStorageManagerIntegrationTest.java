package me.ezzedine.mohammed.personalspace.category.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.category.core.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        CategoryRepository.class,
        CategoryStorageManager.class,
})
class CategoryStorageManagerIntegrationTest extends DatabaseIntegrationTest {

    public static final String NAME = UUID.randomUUID().toString();
    public static final int ORDER = new Random().nextInt();
    @Autowired
    private CategoryRepository repository;
    private CategoryStorageManager storageManager;

    @BeforeEach
    void setUp() {
        storageManager = new CategoryStorageManager(repository);
        repository.deleteAll();
    }

    @Nested
    @DisplayName("When checking if an article category exists given its id")
    class CheckingIfCategoryExistsByIdIntegrationTest {

        @Test
        @DisplayName("it should return false if no such id is found")
        void it_should_return_false_if_no_such_id_is_found() {
            repository.save(getEntity(UUID.randomUUID().toString()));
            assertFalse(storageManager.categoryExists(UUID.randomUUID().toString()));
        }

        @Test
        @DisplayName("it should return true if the matching id exists in the store")
        void it_should_return_true_if_the_matching_id_exists_in_the_store() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));
            assertTrue(storageManager.categoryExists(id));
        }
    }

    @Nested
    @DisplayName("When fetching the list of existing categories in order")
    class FetchingListOfCategoriesIntegrationTest {
        @Test
        @DisplayName("it should return an empty list if none exist")
        void it_should_return_an_empty_list_if_none_exist() {
            assertEquals(Collections.emptyList(), storageManager.fetchAllOrderedByOrder());
        }

        @Test
        @DisplayName("it should return a list of one item when one category exists")
        void it_should_return_a_list_of_one_item_when_one_category_exists() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));
            List<Category> articleCategories = storageManager.fetchAllOrderedByOrder();
            assertEquals(1, articleCategories.size());
            assertEquals(id, articleCategories.get(0).getId());
            assertEquals(NAME, articleCategories.get(0).getName());
            assertEquals(ORDER, articleCategories.get(0).getOrder());
        }

        @Test
        @DisplayName("it should return the list of all existing categories sorted in order by their order")
        void it_should_return_the_list_of_all_existing_categories_sorted_in_order_by_their_order() {
            CategoryEntity thirdCategory = getEntityWithOrder(8);
            CategoryEntity firstCategory = getEntityWithOrder(2);
            CategoryEntity secondCategory = getEntityWithOrder(5);
            repository.save(thirdCategory);
            repository.save(firstCategory);
            repository.save(secondCategory);

            List<Category> categories = storageManager.fetchAllOrderedByOrder();
            assertEquals(3, categories.size());
            assertEquals(firstCategory.getId(), categories.get(0).getId());
            assertEquals(secondCategory.getId(), categories.get(1).getId());
            assertEquals(thirdCategory.getId(), categories.get(2).getId());
        }
    }

    @Nested
    @DisplayName("When fetching an article category given its id")
    class FetchingCategoryByIdIntegrationTest {
        @Test
        @DisplayName("it should return an empty optional if the category does not exist")
        void it_should_return_an_empty_optional_if_the_category_does_not_exist() {
            Optional<Category> optionalCategory = storageManager.fetch(UUID.randomUUID().toString());
            assertTrue(optionalCategory.isEmpty());
        }

        @Test
        @DisplayName("it should return the category when it is found")
        void it_should_return_the_category_when_it_is_found() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            Optional<Category> optionalCategory = storageManager.fetch(id);
            assertTrue(optionalCategory.isPresent());
            assertEquals(id, optionalCategory.get().getId());
            assertEquals(NAME, optionalCategory.get().getName());
            assertEquals(ORDER, optionalCategory.get().getOrder());
        }
    }

    @Nested
    @DisplayName("When fetching the category with the highest order")
    class FetchingCategoryWithHighestOrderIntegrationTest {

        @Test
        @DisplayName("should return an empty result if no categories exist in the storage")
        void should_return_an_empty_result_if_no_categories_exist_in_the_storage() {
            Optional<Category> category = storageManager.fetchCategoryWithHighestOrder();
            assertTrue(category.isEmpty());
        }

        @Test
        @DisplayName("should return the only category existing in the storage when it is the case")
        void should_return_the_only_category_existing_in_the_storage_when_it_is_the_case() {
            repository.save(getEntityWithOrder(1));
            Optional<Category> category = storageManager.fetchCategoryWithHighestOrder();
            assertTrue(category.isPresent());
            assertEquals(1, category.get().getOrder());
        }

        @Test
        @DisplayName("should return the correct category when more than one exist in the storage")
        void should_return_the_correct_category_when_more_than_one_exist_in_the_storage() {
            repository.save(getEntityWithOrder(1));
            repository.save(getEntityWithOrder(2));
            Optional<Category> category = storageManager.fetchCategoryWithHighestOrder();
            assertTrue(category.isPresent());
            assertEquals(2, category.get().getOrder());

        }
    }

    @Nested
    @DisplayName("When persisting new article category entity")
    class PersistingCategoryIntegrationTest {

        @Test
        @DisplayName("the new entity should be persisted in the database")
        void the_new_entity_should_be_persisted_in_the_database() {
            String id = UUID.randomUUID().toString();
            storageManager.persist(getCategory(id));
            List<CategoryEntity> allCategories = repository.findAll();
            assertEquals(1, allCategories.size());
            assertEquals(id, allCategories.get(0).getId());
            assertEquals(NAME, allCategories.get(0).getName());
            assertEquals(ORDER, allCategories.get(0).getOrder());
        }

        @Test
        @DisplayName("it should override any old entity with the same id when found")
        void it_should_override_any_old_entity_with_the_same_id_when_found() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            Category newCategory = Category.builder().id(id).name(UUID.randomUUID().toString()).order(new Random().nextInt()).build();
            storageManager.persist(newCategory);

            List<CategoryEntity> allCategories = repository.findAll();
            assertEquals(1, allCategories.size());
            assertEquals(id, allCategories.get(0).getId());
            assertEquals(newCategory.getName(), allCategories.get(0).getName());
            assertEquals(newCategory.getOrder(), allCategories.get(0).getOrder());
        }
    }

    @Nested
    @DisplayName("When deleting an article category entity")
    class DeletingCategoryIntegrationTest {
        @Test
        @DisplayName("the entity should be deleted from the store")
        void the_entity_should_be_deleted_from_the_store() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            storageManager.delete(id);

            List<CategoryEntity> allCategories = repository.findAll();
            assertEquals(0, allCategories.size());
        }
    }

    private static Category getCategory(String id) {
        return Category.builder()
                .id(id)
                .name(NAME)
                .order(ORDER)
                .build();
    }

    private static CategoryEntity getEntity(String id) {
        return CategoryEntity.builder()
                .id(id)
                .name(NAME)
                .order(ORDER)
                .build();
    }

    private static CategoryEntity getEntityWithOrder(int order) {
        return CategoryEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(NAME)
                .order(order)
                .build();
    }
}