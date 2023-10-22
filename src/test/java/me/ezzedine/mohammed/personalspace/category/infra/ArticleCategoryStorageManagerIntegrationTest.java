package me.ezzedine.mohammed.personalspace.category.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
//import me.ezzedine.mohammed.personalspace.MongoDBContainerTestConfiguration;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        ArticleCategoryRepository.class,
        ArticleCategoryStorageManager.class,
})
@EnableAutoConfiguration
@AutoConfigureDataMongo
@EnableMongoRepositories
class ArticleCategoryStorageManagerIntegrationTest extends DatabaseIntegrationTest {

    public static final String NAME = UUID.randomUUID().toString();
    public static final boolean CAN_BE_DELETED = true;
    @Autowired
    private ArticleCategoryRepository repository;
    private ArticleCategoryStorageManager storageManager;

    @BeforeEach
    void setUp() {
        storageManager = new ArticleCategoryStorageManager(repository);
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
    @DisplayName("When fetching the list of existing article categories")
    class FetchingListOfArticleCategoriesIntegrationTest {
        @Test
        @DisplayName("it should return an empty list if none exist")
        void it_should_return_an_empty_list_if_none_exist() {
            assertEquals(Collections.emptyList(), storageManager.fetchAll());
        }

        @Test
        @DisplayName("it should return a list of one item when one category exists")
        void it_should_return_a_list_of_one_item_when_one_category_exists() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));
            List<ArticleCategory> articleCategories = storageManager.fetchAll();
            assertEquals(1, articleCategories.size());
            assertEquals(id, articleCategories.get(0).getId());
            assertEquals(NAME, articleCategories.get(0).getName());
            assertEquals(CAN_BE_DELETED, articleCategories.get(0).canBeDeleted());
        }
    }

    @Nested
    @DisplayName("When fetching an article category given its id")
    class FetchingArticleCategoryByIdIntegrationTest {
        @Test
        @DisplayName("it should return an empty optional if the category does not exist")
        void it_should_return_an_empty_optional_if_the_category_does_not_exist() {
            Optional<ArticleCategory> optionalCategory = storageManager.fetch(UUID.randomUUID().toString());
            assertTrue(optionalCategory.isEmpty());
        }

        @Test
        @DisplayName("it should return the category when it is found")
        void it_should_return_the_category_when_it_is_found() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            Optional<ArticleCategory> optionalCategory = storageManager.fetch(id);
            assertTrue(optionalCategory.isPresent());
            assertEquals(id, optionalCategory.get().getId());
            assertEquals(NAME, optionalCategory.get().getName());
            assertEquals(CAN_BE_DELETED, optionalCategory.get().canBeDeleted());
        }
    }

    @Nested
    @DisplayName("When persisting new article category entity")
    class PersistingArticleCategoryIntegrationTest {

        @Test
        @DisplayName("the new entity should be persisted in the database")
        void the_new_entity_should_be_persisted_in_the_database() {
            String id = UUID.randomUUID().toString();
            storageManager.persist(getArticleCategory(id));
            List<ArticleCategoryEntity> allCategories = repository.findAll();
            assertEquals(1, allCategories.size());
            assertEquals(id, allCategories.get(0).getId());
            assertEquals(NAME, allCategories.get(0).getName());
            assertEquals(CAN_BE_DELETED, allCategories.get(0).canBeDeleted());
        }

        @Test
        @DisplayName("it should override any old entity with the same id when found")
        void it_should_override_any_old_entity_with_the_same_id_when_found() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            ArticleCategory newCategory = ArticleCategory.builder().id(id).name(UUID.randomUUID().toString())
                    .canBeDeleted(false).build();
            storageManager.persist(newCategory);

            List<ArticleCategoryEntity> allCategories = repository.findAll();
            assertEquals(1, allCategories.size());
            assertEquals(id, allCategories.get(0).getId());
            assertEquals(newCategory.getName(), allCategories.get(0).getName());
            assertEquals(newCategory.canBeDeleted(), allCategories.get(0).canBeDeleted());
        }
    }

    @Nested
    @DisplayName("When deleting an article category entity")
    class DeletingArticleCategoryIntegrationTest {
        @Test
        @DisplayName("the entity should be deleted from the store")
        void the_entity_should_be_deleted_from_the_store() {
            String id = UUID.randomUUID().toString();
            repository.save(getEntity(id));

            storageManager.delete(id);

            List<ArticleCategoryEntity> allCategories = repository.findAll();
            assertEquals(0, allCategories.size());
        }
    }

    private static ArticleCategory getArticleCategory(String id) {
        return ArticleCategory.builder()
                .id(id)
                .name(NAME)
                .canBeDeleted(CAN_BE_DELETED)
                .build();
    }

    private static ArticleCategoryEntity getEntity(String id) {
        return ArticleCategoryEntity.builder()
                .id(id)
                .name(NAME)
                .canBeDeleted(CAN_BE_DELETED)
                .build();
    }
}