package me.ezzedine.mohammed.personalspace.article.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.category.core.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @Autowired
    private ArticleMongoRepository repository;

    @Autowired
    private ArticleStorageManager storageManager;
    private Category category;

    @BeforeEach
    void setUp() {
        category = mock(Category.class);
        when(category.getId()).thenReturn(CATEGORY_ID);
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
            return Article.builder().id(ID).category(category).title(TITLE).description(DESCRIPTION).content(CONTENT).build();
        }
    }
}