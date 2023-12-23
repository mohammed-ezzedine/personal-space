package me.ezzedine.mohammed.personalspace.article.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        ArticleStorageManager.class,
        ArticleMongoRepository.class
})
class ArticleStorageManagerIntegrationTest extends DatabaseIntegrationTest {

    public static final String ID = UUID.randomUUID().toString();
    public static final String CATEGORY_ID = UUID.randomUUID().toString();
    public static final String TITLE = UUID.randomUUID().toString();
    public static final String DESCRIPTION = UUID.randomUUID().toString();
    public static final String CONTENT = UUID.randomUUID().toString();
    public static final String THUMBNAIL_IMAGE_URL = UUID.randomUUID().toString();
    @Autowired
    private ArticleMongoRepository repository;

    @Autowired
    private ArticleStorageManager storageManager;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("When saving an article in the storage")
    class SavingArticleInStorageIntegrationTest {

        @Test
        @DisplayName("the article should be persisted in the storage")
        void the_article_should_be_persisted_in_the_storage() {
            storageManager.save(getArticle());

            List<ArticleEntity> allArticles = repository.findAll();
            assertEquals(1, allArticles.size());
            assertEquals(ID, allArticles.get(0).getId());
            assertEquals(CATEGORY_ID, allArticles.get(0).getCategoryId());
            assertEquals(TITLE, allArticles.get(0).getTitle());
            assertEquals(DESCRIPTION, allArticles.get(0).getDescription());
            assertEquals(CONTENT, allArticles.get(0).getContent());
        }

        @Test
        @DisplayName("should override any existing article with the same id")
        void should_override_any_existing_article_with_the_same_id() {
            repository.save(getRandomArticleEntity());

            storageManager.save(getArticle());

            List<ArticleEntity> allArticles = repository.findAll();
            assertEquals(1, allArticles.size());
            assertEquals(ID, allArticles.get(0).getId());
            assertEquals(CATEGORY_ID, allArticles.get(0).getCategoryId());
            assertEquals(TITLE, allArticles.get(0).getTitle());
            assertEquals(DESCRIPTION, allArticles.get(0).getDescription());
            assertEquals(CONTENT, allArticles.get(0).getContent());
        }

        private static ArticleEntity getRandomArticleEntity() {
            return ArticleEntity.builder().id(ID).title(UUID.randomUUID().toString()).categoryId(UUID.randomUUID().toString())
                    .content(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).build();
        }

        private Article getArticle() {
            return Article.builder().id(ID).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                    .thumbnailImageUrl(THUMBNAIL_IMAGE_URL).build();
        }
    }

    @Nested
    @DisplayName("When fetching an article from the storage")
    class FetchingArticleFromStorageIntegrationTest {

        @Test
        @DisplayName("it should return an empty optional if the article does not exist")
        void it_should_return_an_empty_optional_if_the_article_does_not_exist() {
            Optional<Article> optionalArticle = storageManager.fetch(UUID.randomUUID().toString());
            assertTrue(optionalArticle.isEmpty());
        }

        @Test
        @DisplayName("it should return the article when it exists")
        void it_should_return_the_article_when_it_exists() {
            repository.save(getEntity());
            Optional<Article> optionalArticle = storageManager.fetch(ID);
            assertTrue(optionalArticle.isPresent());
            assertEquals(ID, optionalArticle.get().getId());
            assertEquals(DESCRIPTION, optionalArticle.get().getDescription());
            assertEquals(CONTENT, optionalArticle.get().getContent());
            assertEquals(TITLE, optionalArticle.get().getTitle());
            assertEquals(CATEGORY_ID, optionalArticle.get().getCategoryId());
        }
    }

    @Nested
    @DisplayName("When fetching the list of articles in the storage")
    class FetchingListOfArticlesIntegrationTest {
        @Test
        @DisplayName("should return an empty list when no articles exist")
        void should_return_an_empty_list_when_no_articles_exist() {
            assertTrue(storageManager.fetchAll().isEmpty());
        }

        @Test
        @DisplayName("should return a list of one article when only one exists")
        void should_return_a_list_of_one_article_when_only_one_exists() {
            repository.save(getEntity());
            List<Article> articles = storageManager.fetchAll();
            assertEquals(1, articles.size());
            assertEquals(ID, articles.get(0).getId());
            assertEquals(TITLE, articles.get(0).getTitle());
            assertEquals(DESCRIPTION, articles.get(0).getDescription());
            assertEquals(CONTENT, articles.get(0).getContent());
            assertEquals(CATEGORY_ID, articles.get(0).getCategoryId());
        }
    }

    private ArticleEntity getEntity() {
        return ArticleEntity.builder().id(ID).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION)
                .content(CONTENT).thumbnailImageUrl(THUMBNAIL_IMAGE_URL).build();
    }
}