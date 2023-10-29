package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.category.core.CategoryFetcher;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    public static final String CATEGORY_ID = UUID.randomUUID().toString();
    public static final String TITLE = UUID.randomUUID().toString();
    public static final String CONTENT = UUID.randomUUID().toString();
    public static final String DESCRIPTION = UUID.randomUUID().toString();
    public static final String ARTICLE_ID = UUID.randomUUID().toString();
    private ArticleStorage storage;
    private ArticleService service;
    private CategoryFetcher categoryFetcher;
    private ArticleIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        storage = mock(ArticleStorage.class);
        categoryFetcher = mock(CategoryFetcher.class);
        idGenerator = mock(ArticleIdGenerator.class);
        service = new ArticleService(storage, categoryFetcher, idGenerator);
    }

    @Nested
    @DisplayName("When creating a new article")
    class CreatingArticleTest {


        @BeforeEach
        void setUp() {
            when(idGenerator.generate()).thenReturn(ARTICLE_ID);
        }

        @Test
        @DisplayName("should throw an exception if the category does not exist")
        void should_throw_an_exception_if_the_category_does_not_exist() throws CategoryNotFoundException {
            when(categoryFetcher.fetch(CATEGORY_ID)).thenThrow(CategoryNotFoundException.class);
            assertThrows(CategoryNotFoundException.class, () -> service.create(getRequest()));
        }

        @Test
        @DisplayName("should generate a unique id for the new article")
        void should_generate_a_unique_id_for_the_new_article() throws CategoryNotFoundException {
            service.create(getRequest());
            verify(idGenerator).generate();
        }

        @Test
        @DisplayName("should save the new article in the storage")
        void should_save_the_new_article_in_the_storage() throws CategoryNotFoundException {
            service.create(getRequest());

            ArgumentCaptor<Article> argumentCaptor = ArgumentCaptor.forClass(Article.class);
            verify(storage).save(argumentCaptor.capture());

            assertEquals(ARTICLE_ID, argumentCaptor.getValue().getId());
            assertEquals(TITLE, argumentCaptor.getValue().getTitle());
            assertEquals(CATEGORY_ID, argumentCaptor.getValue().getCategoryId());
            assertEquals(CONTENT, argumentCaptor.getValue().getContent());
            assertEquals(DESCRIPTION, argumentCaptor.getValue().getDescription());
        }

        @Test
        @DisplayName("should return the id of the newly created article")
        void should_return_the_id_of_the_newly_created_article() throws CategoryNotFoundException {
            ArticleCreationResult result = service.create(getRequest());
            assertEquals(ARTICLE_ID, result.getId());
        }

        private static ArticleCreationRequest getRequest() {
            return ArticleCreationRequest.builder().categoryId(CATEGORY_ID).title(TITLE).content(CONTENT).description(DESCRIPTION).build();
        }
    }

    @Nested
    @DisplayName("When fetching an article's details")
    class FetchingArticleDetailsTest {

        @BeforeEach
        void setUp() {
            when(storage.fetch(ARTICLE_ID)).thenReturn(Optional.of(getArticle()));
        }

        @Test
        @DisplayName("it should fail if the article does not exist")
        void it_should_fail_if_the_article_does_not_exist() {
            when(storage.fetch(ARTICLE_ID)).thenReturn(Optional.empty());
            assertThrows(ArticleNotFoundException.class, () -> service.fetch(ARTICLE_ID));
        }

        @Test
        @DisplayName("it should return the article details")
        void it_should_return_the_article_details() throws ArticleNotFoundException {
            Article article = service.fetch(ARTICLE_ID);
            assertEquals(ARTICLE_ID, article.getId());
            assertEquals(TITLE, article.getTitle());
            assertEquals(DESCRIPTION, article.getDescription());
            assertEquals(CONTENT, article.getContent());
            assertEquals(CATEGORY_ID, article.getCategoryId());
        }
    }

    private Article getArticle() {
        return Article.builder().id(ARTICLE_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                .categoryId(CATEGORY_ID).build();
    }
}