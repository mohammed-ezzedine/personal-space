package me.ezzedine.mohammed.personalspace.article.infra.highlight;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.MongoConfiguration;
import me.ezzedine.mohammed.personalspace.article.core.highlight.HighlightedArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        HighlightedArticleMongoRepository.class,
        ArticlesHighlightStorageManager.class,
        MongoConfiguration.class
})
class ArticlesHighlightStorageManagerIntegrationTest extends DatabaseIntegrationTest {

    public static final String FIRST_ARTICLE_ID = UUID.randomUUID().toString();
    public static final String SECOND_ARTICLE_ID = UUID.randomUUID().toString();
    public static final int FIRST_RANK = 1;
    public static final int THIRD_RANK = 3;
    public static final int SECOND_RANK = 2;
    public static final String THIRD_ARTICLE_ID = UUID.randomUUID().toString();
    @Autowired
    private HighlightedArticleMongoRepository repository;

    @Autowired
    private ArticlesHighlightStorageManager storageManager;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("When saving a list of highlighted articles")
    class SavingHighlightedArticlesIntegrationTest {
        @Test
        @DisplayName("the data should be saved in the storage")
        void the_data_should_be_saved_in_the_storage() {
            storageManager.save(List.of(getHighlightedArticle(FIRST_ARTICLE_ID, THIRD_RANK), getHighlightedArticle(SECOND_ARTICLE_ID, FIRST_RANK)));
            List<HighlightedArticleEntity> entities = repository.findAll();
            assertEquals(2, entities.size());
            assertEquals(FIRST_ARTICLE_ID, entities.get(0).getArticleId());
            assertEquals(THIRD_RANK, entities.get(0).getRank());
            assertEquals(SECOND_ARTICLE_ID, entities.get(1).getArticleId());
            assertEquals(FIRST_RANK, entities.get(1).getRank());
        }

        @Test
        @DisplayName("any old data should be overriden")
        void any_old_data_should_be_overriden() {
            repository.saveAll(List.of(getEntity(FIRST_ARTICLE_ID, SECOND_RANK), getEntity(THIRD_ARTICLE_ID, FIRST_RANK)));

            storageManager.save(List.of(getHighlightedArticle(SECOND_ARTICLE_ID, THIRD_RANK), getHighlightedArticle(FIRST_ARTICLE_ID, FIRST_RANK)));
            List<HighlightedArticleEntity> entities = repository.findAll();
            assertEquals(2, entities.size());
            assertEquals(SECOND_ARTICLE_ID, entities.get(0).getArticleId());
            assertEquals(THIRD_RANK, entities.get(0).getRank());
            assertEquals(FIRST_ARTICLE_ID, entities.get(1).getArticleId());
            assertEquals(FIRST_RANK, entities.get(1).getRank());
        }
    }

    @Nested
    @DisplayName("When fetching the list of highlighted articles")
    class FetchingHighlightedArticlesIntegrationTest {

        @Test
        @DisplayName("should return an empty list when no article is highlighted")
        void should_return_an_empty_list_when_no_article_is_highlighted() {
            List<HighlightedArticle> highlightedArticles = storageManager.getArticleHighlightsSummary();
            assertTrue(highlightedArticles.isEmpty());
        }

        @Test
        @DisplayName("should return the list of highlighted articles in increasing order of rank")
        void should_return_the_list_of_highlighted_articles_in_increasing_order_of_rank() {
            repository.saveAll(List.of(getEntity(FIRST_ARTICLE_ID, THIRD_RANK), getEntity(SECOND_ARTICLE_ID, SECOND_RANK)));

            List<HighlightedArticle> highlightedArticles = storageManager.getArticleHighlightsSummary();
            assertEquals(2, highlightedArticles.size());
            assertEquals(SECOND_ARTICLE_ID, highlightedArticles.get(0).getArticleId());
            assertEquals(SECOND_RANK, highlightedArticles.get(0).getHighlightRank());
            assertEquals(FIRST_ARTICLE_ID, highlightedArticles.get(1).getArticleId());
            assertEquals(THIRD_RANK, highlightedArticles.get(1).getHighlightRank());
        }
    }

    private static HighlightedArticle getHighlightedArticle(String articleId, int rank) {
        return HighlightedArticle.builder().articleId(articleId).highlightRank(rank).build();
    }

    private static HighlightedArticleEntity getEntity(String articleId, int rank) {
        return HighlightedArticleEntity.builder().articleId(articleId).rank(rank).build();
    }

}