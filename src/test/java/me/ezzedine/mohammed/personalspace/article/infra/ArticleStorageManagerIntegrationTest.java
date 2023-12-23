package me.ezzedine.mohammed.personalspace.article.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.util.pagination.FetchCriteria;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
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
    public static final String KEYWORD = UUID.randomUUID().toString();
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
            assertEquals(THUMBNAIL_IMAGE_URL, allArticles.get(0).getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), allArticles.get(0).getKeywords());
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
            assertEquals(THUMBNAIL_IMAGE_URL, allArticles.get(0).getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), allArticles.get(0).getKeywords());
        }

        private static ArticleEntity getRandomArticleEntity() {
            return ArticleEntity.builder().id(ID).title(UUID.randomUUID().toString()).categoryId(UUID.randomUUID().toString())
                    .content(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).thumbnailImageUrl(UUID.randomUUID().toString())
                    .keywords(List.of(UUID.randomUUID().toString())).build();
        }

        private Article getArticle() {
            return Article.builder().id(ID).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                    .thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).build();
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
            assertEquals(THUMBNAIL_IMAGE_URL, optionalArticle.get().getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), optionalArticle.get().getKeywords());
        }
    }

    @Nested
    @DisplayName("When fetching the list of articles in the storage")
    class FetchingListOfArticlesIntegrationTest {
        @Test
        @DisplayName("should return an empty list when no articles exist")
        void should_return_an_empty_list_when_no_articles_exist() {
            FetchCriteria fetchCriteria = FetchCriteria.builder().startingPageIndex(0).maximumPageSize(100).build();
            Page<Article> articles = storageManager.fetchAll(fetchCriteria);
            assertEquals(0, articles.getTotalSize());
            assertTrue(articles.getItems().isEmpty());
        }

        @Test
        @DisplayName("should return a list of one article when only one exists")
        void should_return_a_list_of_one_article_when_only_one_exists() {
            repository.save(getEntity());
            FetchCriteria fetchCriteria = FetchCriteria.builder().startingPageIndex(0).maximumPageSize(100).build();
            Page<Article> articles = storageManager.fetchAll(fetchCriteria);
            assertEquals(1, articles.getTotalSize());
            assertEquals(ID, articles.getItems().get(0).getId());
            assertEquals(TITLE, articles.getItems().get(0).getTitle());
            assertEquals(DESCRIPTION, articles.getItems().get(0).getDescription());
            assertEquals(CONTENT, articles.getItems().get(0).getContent());
            assertEquals(CATEGORY_ID, articles.getItems().get(0).getCategoryId());
            assertEquals(THUMBNAIL_IMAGE_URL, articles.getItems().get(0).getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), articles.getItems().get(0).getKeywords());
        }

        @Test
        @DisplayName("should only return one page when the page size is reached")
        void should_only_return_one_page_when_the_page_size_is_reached() {
            String firstArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(firstArticleId));
            String secondArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(secondArticleId));
            String thirdArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(thirdArticleId));
            String forthArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(forthArticleId));

            FetchCriteria fetchCriteria = FetchCriteria.builder().startingPageIndex(0).maximumPageSize(2).build();
            Page<Article> articles = storageManager.fetchAll(fetchCriteria);
            assertEquals(4, articles.getTotalSize());
            assertEquals(2, articles.getItems().size());
            assertEquals(firstArticleId, articles.getItems().get(0).getId());
            assertEquals(secondArticleId, articles.getItems().get(1).getId());
        }

        @Test
        @DisplayName("should skip the first page when the user asks for the second page of articles")
        void should_skip_the_first_page_when_the_user_asks_for_the_second_page_of_articles() {
            String firstArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(firstArticleId));
            String secondArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(secondArticleId));
            String thirdArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(thirdArticleId));
            String forthArticleId = UUID.randomUUID().toString();
            repository.save(getEntity(forthArticleId));

            FetchCriteria fetchCriteria = FetchCriteria.builder().startingPageIndex(1).maximumPageSize(2).build();
            Page<Article> articles = storageManager.fetchAll(fetchCriteria);

            assertEquals(4, articles.getTotalSize());
            assertEquals(2, articles.getItems().size());
            assertEquals(thirdArticleId, articles.getItems().get(0).getId());
            assertEquals(forthArticleId, articles.getItems().get(1).getId());
        }
    }

    private ArticleEntity getEntity(String id) {
        return ArticleEntity.builder().id(id).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION)
                .content(CONTENT).thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).build();
    }

    private ArticleEntity getEntity() {
        return getEntity(ID);
    }
}