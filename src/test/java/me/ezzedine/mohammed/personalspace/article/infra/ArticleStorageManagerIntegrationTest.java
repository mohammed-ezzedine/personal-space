package me.ezzedine.mohammed.personalspace.article.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.MongoConfiguration;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticlesFetchCriteria;
import me.ezzedine.mohammed.personalspace.article.infra.highlight.HighlightedArticleEntity;
import me.ezzedine.mohammed.personalspace.article.infra.highlight.HighlightedArticleMongoRepository;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import me.ezzedine.mohammed.personalspace.util.pagination.PaginationCriteria;
import me.ezzedine.mohammed.personalspace.util.sort.SortingCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        ArticleStorageManager.class,
        ArticleMongoRepository.class,
        MongoConfiguration.class
})
class ArticleStorageManagerIntegrationTest extends DatabaseIntegrationTest {

    public static final String ID = UUID.randomUUID().toString();
    public static final String CATEGORY_ID = UUID.randomUUID().toString();
    public static final String TITLE = UUID.randomUUID().toString();
    public static final String DESCRIPTION = UUID.randomUUID().toString();
    public static final String CONTENT = UUID.randomUUID().toString();
    public static final String THUMBNAIL_IMAGE_URL = UUID.randomUUID().toString();
    public static final String KEYWORD = UUID.randomUUID().toString();
    public static final boolean HIDDEN = true;
    public static final String ESTIMATED_READING_TIME = UUID.randomUUID().toString();
    @Autowired
    private ArticleMongoRepository repository;

    @Autowired
    private ArticleStorageManager storageManager;

    @MockBean
    private HighlightedArticleMongoRepository highlightedArticleRepository;

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
            assertEquals(HIDDEN, allArticles.get(0).isHidden());
            assertEquals(ESTIMATED_READING_TIME, allArticles.get(0).getEstimatedReadingTime());
        }

//        @Test
//        @DisplayName("the stored article should have the current date as the value for its creation date")
//        void the_stored_article_should_have_the_current_date_as_the_value_for_its_creation_date() {
//            storageManager.save(getArticle());
//
//            List<ArticleEntity> allArticles = repository.findAll();
//            assertEquals(1, allArticles.size());
//            assertNotNull(allArticles.get(0).getCreatedDate());
//            assertEquals(allArticles.get(0).getCreatedDate(), allArticles.get(0).getLastModifiedDate());
//        }

        @Test
        @DisplayName("should override any existing article with the same id")
        void should_override_any_existing_article_with_the_same_id() {
            ArticleEntity entity = repository.save(getRandomArticleEntity());

            Article article = getArticle();
            article.setVersion(entity.getVersion());
            article.setCreatedDate(entity.getCreatedDate());
            article.setLastModifiedDate(entity.getLastModifiedDate());
            storageManager.save(article);

            List<ArticleEntity> allArticles = repository.findAll();
            assertEquals(1, allArticles.size());
            assertEquals(ID, allArticles.get(0).getId());
            assertEquals(CATEGORY_ID, allArticles.get(0).getCategoryId());
            assertEquals(TITLE, allArticles.get(0).getTitle());
            assertEquals(DESCRIPTION, allArticles.get(0).getDescription());
            assertEquals(CONTENT, allArticles.get(0).getContent());
            assertEquals(THUMBNAIL_IMAGE_URL, allArticles.get(0).getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), allArticles.get(0).getKeywords());
            assertEquals(HIDDEN, allArticles.get(0).isHidden());
            assertEquals(ESTIMATED_READING_TIME, allArticles.get(0).getEstimatedReadingTime());
        }

//        @Test
//        @DisplayName("should update the last modified date when overriding an existing article")
//        void should_update_the_last_modified_date_when_overriding_an_existing_article() {
//            ArticleEntity entity = repository.save(getRandomArticleEntity());
//
//            Article article = getArticle();
//            article.setVersion(entity.getVersion());
//            article.setCreatedDate(entity.getCreatedDate());
//            article.setLastModifiedDate(entity.getLastModifiedDate());
//            storageManager.save(article);
//
//            List<ArticleEntity> allArticles = repository.findAll();
//            assertEquals(1, allArticles.size());
//            assertNotEquals(entity.getLastModifiedDate(), allArticles.get(0).getLastModifiedDate());
//        }

//        @Test
//        @DisplayName("should not update the created date when overriding an existing article")
//        void should_not_update_the_created_date_when_overriding_an_existing_article() {
//            ArticleEntity entity = repository.save(getRandomArticleEntity());
//
//            Article article = getArticle();
//            article.setVersion(entity.getVersion());
//            article.setCreatedDate(entity.getCreatedDate());
//            article.setLastModifiedDate(entity.getLastModifiedDate());
//            storageManager.save(article);
//
//            List<ArticleEntity> allArticles = repository.findAll();
//            assertEquals(1, allArticles.size());
//            assertEquals(entity.getCreatedDate().truncatedTo(ChronoUnit.SECONDS), allArticles.get(0).getCreatedDate().truncatedTo(ChronoUnit.SECONDS));
//        }

        private static ArticleEntity getRandomArticleEntity() {
            return ArticleEntity.builder().id(ID).title(UUID.randomUUID().toString()).categoryId(UUID.randomUUID().toString())
                    .content(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).thumbnailImageUrl(UUID.randomUUID().toString())
                    .keywords(List.of(UUID.randomUUID().toString())).hidden(false).estimatedReadingTime(UUID.randomUUID().toString()).build();
        }

        private Article getArticle() {
            return Article.builder().id(ID).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                    .thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).hidden(HIDDEN).estimatedReadingTime(ESTIMATED_READING_TIME).build();
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

//        @Test
//        @DisplayName("it should return the article when it exists")
//        void it_should_return_the_article_when_it_exists() {
//            repository.save(getEntity());
//            Optional<Article> optionalArticle = storageManager.fetch(ID);
//            assertTrue(optionalArticle.isPresent());
//            assertEquals(ID, optionalArticle.get().getId());
//            assertEquals(DESCRIPTION, optionalArticle.get().getDescription());
//            assertEquals(CONTENT, optionalArticle.get().getContent());
//            assertEquals(TITLE, optionalArticle.get().getTitle());
//            assertEquals(CATEGORY_ID, optionalArticle.get().getCategoryId());
//            assertEquals(THUMBNAIL_IMAGE_URL, optionalArticle.get().getThumbnailImageUrl());
//            assertEquals(List.of(KEYWORD), optionalArticle.get().getKeywords());
//            assertNotNull(optionalArticle.get().getCreatedDate());
//            assertNotNull(optionalArticle.get().getLastModifiedDate());
//            assertEquals(HIDDEN, optionalArticle.get().isHidden());
//            assertEquals(ESTIMATED_READING_TIME, optionalArticle.get().getEstimatedReadingTime());
//        }
    }

    @Nested
    @DisplayName("When fetching the list of articles in the storage")
    class FetchingListOfArticlesIntegrationTest {

        @Nested
        @DisplayName("When specifying pagination criteria")
        class FetchingPaginatedArticlesIntegrationTest {

            @Test
            @DisplayName("should return an empty list when no articles exist")
            void should_return_an_empty_list_when_no_articles_exist() {
                PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(0).maximumPageSize(100).build();
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build());
                assertEquals(0, articles.getTotalSize());
                assertTrue(articles.getItems().isEmpty());
            }

//            @Test
//            @DisplayName("should return all the articles when they are less than the size of the first page")
//            void should_return_all_the_articles_when_they_are_less_than_the_size_of_the_first_page() {
//                repository.save(getEntity());
//                PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(0).maximumPageSize(100).build();
//                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build());
//                assertEquals(1, articles.getTotalSize());
//                assertEquals(ID, articles.getItems().get(0).getId());
//                assertEquals(TITLE, articles.getItems().get(0).getTitle());
//                assertEquals(DESCRIPTION, articles.getItems().get(0).getDescription());
//                assertEquals(CONTENT, articles.getItems().get(0).getContent());
//                assertEquals(CATEGORY_ID, articles.getItems().get(0).getCategoryId());
//                assertEquals(THUMBNAIL_IMAGE_URL, articles.getItems().get(0).getThumbnailImageUrl());
//                assertEquals(List.of(KEYWORD), articles.getItems().get(0).getKeywords());
//                assertNotNull(articles.getItems().get(0).getCreatedDate());
//                assertNotNull(articles.getItems().get(0).getLastModifiedDate());
//                assertEquals(HIDDEN, articles.getItems().get(0).isHidden());
//                assertEquals(ESTIMATED_READING_TIME, articles.getItems().get(0).getEstimatedReadingTime());
//            }

            @Test
            @DisplayName("should return the first page only when there are more articles than the mentioned size")
            void should_return_the_first_page_only_when_there_are_more_articles_than_the_mentioned_size() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                String thirdArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(thirdArticleId));
                String forthArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(forthArticleId));

                PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(0).maximumPageSize(2).build();
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build());
                assertEquals(4, articles.getTotalSize());
                assertEquals(2, articles.getItems().size());
                assertEquals(firstArticleId, articles.getItems().get(0).getId());
                assertEquals(secondArticleId, articles.getItems().get(1).getId());
            }

            @Test
            @DisplayName("should jump to the elements of the page index specified in the criteria")
            void should_jump_to_the_elements_of_the_page_index_specified_in_the_criteria() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                String thirdArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(thirdArticleId));
                String forthArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(forthArticleId));

                PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(2).maximumPageSize(1).build();
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build());
                assertEquals(4, articles.getTotalSize());
                assertEquals(1, articles.getItems().size());
                assertEquals(thirdArticleId, articles.getItems().get(0).getId());
            }

            @Test
            @DisplayName("should return an empty list if there are not enough articles to reach the mentioned page index")
            void should_return_an_empty_list_if_there_are_not_enough_articles_to_reach_the_mentioned_page_index() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                String thirdArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(thirdArticleId));
                String forthArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(forthArticleId));

                PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(2).maximumPageSize(10).build();
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().paginationCriteria(paginationCriteria).build());
                assertEquals(4, articles.getTotalSize());
                assertEquals(0, articles.getItems().size());
            }
        }

        @Nested
        @DisplayName("When not specifying a pagination criteria")
        class FetchingNonPaginatedArticlesIntegrationTest {
            @Test
            @DisplayName("should return an empty list when no article exists")
            void should_return_an_empty_list_when_no_article_exists() {
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().build());
                assertEquals(0, articlePage.getTotalSize());
                assertEquals(0, articlePage.getItems().size());
            }

            @Test
            @DisplayName("should return all the articles in the storage when they exist")
            void should_return_all_the_articles_in_the_storage_when_they_exist() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                String thirdArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(thirdArticleId));
                String forthArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(forthArticleId));

                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().build());
                assertEquals(4, articles.getTotalSize());
                assertEquals(4, articles.getItems().size());
                assertEquals(firstArticleId, articles.getItems().get(0).getId());
                assertEquals(secondArticleId, articles.getItems().get(1).getId());
                assertEquals(thirdArticleId, articles.getItems().get(2).getId());
                assertEquals(forthArticleId, articles.getItems().get(3).getId());
            }
        }

        @Nested
        @DisplayName("When filtering on the highlighted articles")
        class FetchingHighlightedArticlesIntegrationTest {
            @Test
            @DisplayName("should return an empty list when no article exists")
            void should_return_an_empty_list_when_no_article_exists() {
                when(highlightedArticleRepository.findAll()).thenReturn(Collections.emptyList());
                List<Article> articles = storageManager.fetchHighlightedArticles();
                assertEquals(0, articles.size());
            }

            @Test
            @DisplayName("should return an empty list when fetching the highlighted articles and no article is highlighted")
            void should_return_an_empty_list_when_fetching_the_highlighted_articles_and_no_article_is_highlighted() {
                when(highlightedArticleRepository.findAll()).thenReturn(Collections.emptyList());
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));

                List<Article> articles = storageManager.fetchHighlightedArticles();
                assertEquals(0, articles.size());
            }

            @Test
            @DisplayName("should return only the highlighted articles when asked for them")
            void should_return_only_the_highlighted_articles_when_asked_for_them() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                when(highlightedArticleRepository.findAll()).thenReturn(List.of(getHighlightedArticle(secondArticleId)));
                List<Article> articles = storageManager.fetchHighlightedArticles();
                assertEquals(1, articles.size());
                assertEquals(secondArticleId, articles.get(0).getId());
            }

            @Test
            @DisplayName("should sort the highlighted articles in the increasing order of their rank")
            void should_sort_the_highlighted_articles_in_the_increasing_order_of_their_rank() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                when(highlightedArticleRepository.findAll()).thenReturn(List.of(getHighlightedArticle(firstArticleId, 2), getHighlightedArticle(secondArticleId, 1)));
                List<Article> articles = storageManager.fetchHighlightedArticles();
                assertEquals(2, articles.size());
                assertEquals(secondArticleId, articles.get(0).getId());
                assertEquals(firstArticleId, articles.get(1).getId());
            }
        }

        @Nested
        @DisplayName("When filtering on the non-highlighted articles")
        class FetchingNonHighlightedArticlesIntegrationTest {
            @Test
            @DisplayName("should return an empty list when no article exists")
            void should_return_an_empty_list_when_no_article_exists() {
                when(highlightedArticleRepository.findAll()).thenReturn(Collections.emptyList());
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().highlighted(false).build());
                assertEquals(0, articles.getTotalSize());
                assertEquals(0, articles.getItems().size());
            }

            @Test
            @DisplayName("should return an empty list when fetching the highlighted articles and all articles are highlighted")
            void should_return_an_empty_list_when_fetching_the_highlighted_articles_and_all_articles_are_highlighted() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                when(highlightedArticleRepository.findAll()).thenReturn(List.of(getHighlightedArticle(firstArticleId), getHighlightedArticle(secondArticleId)));

                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().highlighted(false).build());
                assertEquals(0, articles.getTotalSize());
                assertEquals(0, articles.getItems().size());
            }

            @Test
            @DisplayName("should return only the non-highlighted articles when asked for them")
            void should_return_only_the_non_highlighted_articles_when_asked_for_them() {
                String firstArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(firstArticleId));
                String secondArticleId = UUID.randomUUID().toString();
                repository.save(getEntity(secondArticleId));
                when(highlightedArticleRepository.findAll()).thenReturn(List.of(getHighlightedArticle(secondArticleId)));
                Page<Article> articles = storageManager.fetchAll(ArticlesFetchCriteria.builder().highlighted(false).build());
                assertEquals(1, articles.getTotalSize());
                assertEquals(1, articles.getItems().size());
                assertEquals(firstArticleId, articles.getItems().get(0).getId());
            }
        }

        @Nested
        @DisplayName("When filtering on the category id")
        class FetchingArticlesByCategoryIdIntegrationTest {
            @Test
            @DisplayName("should return an empty list when none exist")
            void should_return_an_empty_list_when_none_exist() {
                repository.save(getEntity(ID, CATEGORY_ID));
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().categoryId(UUID.randomUUID().toString()).build());
                assertEquals(0, articlePage.getTotalSize());
                assertEquals(0, articlePage.getItems().size());
            }

            @Test
            @DisplayName("should return the list of matching articles")
            void should_return_the_list_of_matching_articles() {
                String categoryA = UUID.randomUUID().toString();
                String categoryB = UUID.randomUUID().toString();
                ArticleEntity firstEntity = getEntity(UUID.randomUUID().toString(), categoryA);
                ArticleEntity secondEntity = getEntity(UUID.randomUUID().toString(), categoryB);
                ArticleEntity thirdEntity = getEntity(UUID.randomUUID().toString(), categoryA);

                repository.saveAll(List.of(firstEntity, secondEntity, thirdEntity));
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().categoryId(categoryA).build());

                assertEquals(2, articlePage.getTotalSize());
                assertEquals(firstEntity.getId(), articlePage.getItems().get(0).getId());
                assertEquals(thirdEntity.getId(), articlePage.getItems().get(1).getId());
            }

            private ArticleEntity getEntity(String id, String categoryId) {
                return ArticleEntity.builder().id(id).categoryId(categoryId).title(TITLE).description(DESCRIPTION).estimatedReadingTime(ESTIMATED_READING_TIME)
                        .content(CONTENT).thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).hidden(HIDDEN).build();
            }
        }

        @Nested
        @DisplayName("When sorting on a field")
        class FetchSortedArticlesIntegrationTest {
            @Test
            @DisplayName("should return an empty list when no article exists")
            void should_return_an_empty_list_when_no_article_exists() {
                SortingCriteria sortingCriteria = SortingCriteria.builder().field("createdDate").ascendingOrder(true).build();
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().sortingCriteria(sortingCriteria).build());
                assertEquals(0, articlePage.getTotalSize());
                assertEquals(0, articlePage.getItems().size());
            }

            @Test
            @DisplayName("should return the list of articles sorted by the specified field when the user asks for ascending order sorting")
            void should_return_the_list_of_articles_sorted_by_the_specified_field_when_the_user_asks_for_ascending_order_sorting() {
                repository.save(getEntity("a"));
                repository.save(getEntity("c"));
                repository.save(getEntity("b"));

                SortingCriteria sortingCriteria = SortingCriteria.builder().field("id").ascendingOrder(true).build();
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().sortingCriteria(sortingCriteria).build());
                assertEquals(3, articlePage.getTotalSize());
                assertEquals(3, articlePage.getItems().size());
                assertEquals("a", articlePage.getItems().get(0).getId());
                assertEquals("b", articlePage.getItems().get(1).getId());
                assertEquals("c", articlePage.getItems().get(2).getId());
            }

            @Test
            @DisplayName("should return the list of articles sorted by the specified field when the user asks for descending order sorting")
            void should_return_the_list_of_articles_sorted_by_the_specified_field_when_the_user_asks_for_descending_order_sorting() {
                repository.save(getEntity("a"));
                repository.save(getEntity("c"));
                repository.save(getEntity("b"));

                SortingCriteria sortingCriteria = SortingCriteria.builder().field("id").ascendingOrder(false).build();
                Page<Article> articlePage = storageManager.fetchAll(ArticlesFetchCriteria.builder().sortingCriteria(sortingCriteria).build());
                assertEquals(3, articlePage.getTotalSize());
                assertEquals(3, articlePage.getItems().size());
                assertEquals("c", articlePage.getItems().get(0).getId());
                assertEquals("b", articlePage.getItems().get(1).getId());
                assertEquals("a", articlePage.getItems().get(2).getId());
            }
        }

        private static HighlightedArticleEntity getHighlightedArticle(String firstArticleId) {
            return HighlightedArticleEntity.builder().articleId(firstArticleId).build();
        }

        private static HighlightedArticleEntity getHighlightedArticle(String firstArticleId, int rank) {
            return HighlightedArticleEntity.builder().articleId(firstArticleId).rank(rank).build();
        }
    }

    private ArticleEntity getEntity(String id) {
        return ArticleEntity.builder().id(id).categoryId(CATEGORY_ID).title(TITLE).description(DESCRIPTION)
                .content(CONTENT).thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).hidden(HIDDEN)
                .estimatedReadingTime(ESTIMATED_READING_TIME).build();
    }

    private ArticleEntity getEntity() {
        return getEntity(ID);
    }
}