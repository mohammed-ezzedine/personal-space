package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.category.core.CategoryFetcher;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    public static final String CATEGORY_ID = UUID.randomUUID().toString();
    public static final String UPDATED_CATEGORY_ID = UUID.randomUUID().toString();
    public static final String TITLE = UUID.randomUUID().toString();
    public static final String UPDATED_TITLE = UUID.randomUUID().toString();
    public static final String CONTENT = UUID.randomUUID().toString();
    public static final String UPDATED_CONTENT = UUID.randomUUID().toString();
    public static final String DESCRIPTION = UUID.randomUUID().toString();
    public static final String UPDATED_DESCRIPTION = UUID.randomUUID().toString();
    public static final String ARTICLE_ID = UUID.randomUUID().toString();
    public static final String THUMBNAIL_IMAGE_URL = UUID.randomUUID().toString();
    public static final String UPDATED_THUMBNAIL_IMAGE_URL = UUID.randomUUID().toString();
    public static final String KEYWORD = UUID.randomUUID().toString();
    public static final String UPDATED_KEYWORD = UUID.randomUUID().toString();
    public static final Long VERSION = 1L;
    public static final LocalDateTime CREATED_DATE = mock(LocalDateTime.class);
    public static final LocalDateTime LAST_MODIFIED_DATE = mock(LocalDateTime.class);
    public static final boolean HIDDEN = true;
    public static final boolean UPDATED_HIDDEN = false;
    public static final String ESTIMATED_READING_TIME = UUID.randomUUID().toString();
    public static final String UPDATED_ESTIMATED_READING_TIME = UUID.randomUUID().toString();
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
            assertEquals(THUMBNAIL_IMAGE_URL, argumentCaptor.getValue().getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), argumentCaptor.getValue().getKeywords());
            assertEquals(HIDDEN, argumentCaptor.getValue().isHidden());
            assertEquals(ESTIMATED_READING_TIME, argumentCaptor.getValue().getEstimatedReadingTime());
        }

        @Test
        @DisplayName("should return the id of the newly created article")
        void should_return_the_id_of_the_newly_created_article() throws CategoryNotFoundException {
            ArticleCreationResult result = service.create(getRequest());
            assertEquals(ARTICLE_ID, result.getId());
        }

        private static ArticleCreationRequest getRequest() {
            return ArticleCreationRequest.builder().categoryId(CATEGORY_ID).title(TITLE).content(CONTENT).description(DESCRIPTION)
                    .thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD)).hidden(HIDDEN).estimatedReadingTime(ESTIMATED_READING_TIME).build();
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
            assertEquals(THUMBNAIL_IMAGE_URL, article.getThumbnailImageUrl());
            assertEquals(List.of(KEYWORD), article.getKeywords());
            assertEquals(VERSION, article.getVersion());
            assertEquals(CREATED_DATE, article.getCreatedDate());
            assertEquals(LAST_MODIFIED_DATE, article.getLastModifiedDate());
            assertEquals(HIDDEN, article.isHidden());
            assertEquals(ESTIMATED_READING_TIME, article.getEstimatedReadingTime());
        }
    }

    @Nested
    @DisplayName("When fetching all the articles")
    class FetchingAllArticlesTest {

        @Test
        @DisplayName("should retrieve their information from the storage")
        void should_retrieve_their_information_from_the_storage() {
            ArticlesFetchCriteria criteria = mock(ArticlesFetchCriteria.class);
            Page page = mock(Page.class);
            when(storage.fetchAll(criteria)).thenReturn(page);
            Page<Article> articles = service.fetchAll(criteria);
            assertEquals(page, articles);
        }
    }

    @Nested
    @DisplayName("When editing an existing article")
    class EditingArticleTest {

        @Test
        @DisplayName("the user should not be able to select a category that does not exist")
        void the_user_should_not_be_able_to_select_a_category_that_does_not_exist() throws CategoryNotFoundException {
            when(categoryFetcher.fetch(any())).thenThrow(CategoryNotFoundException.class);
            assertThrows(CategoryNotFoundException.class, () -> service.edit(getRequest()));
        }

        @Test
        @DisplayName("the user should not be able to edit an article that does not exist")
        void the_user_should_not_be_able_to_edit_an_article_that_does_not_exist() {
            when(storage.fetch(ARTICLE_ID)).thenReturn(Optional.empty());
            assertThrows(ArticleNotFoundException.class, () -> service.edit(getRequest()));
        }

        @Test
        @DisplayName("the new changes should be saved successfully on the happy path")
        void the_new_changes_should_be_saved_successfully_on_the_happy_path() throws ArticleNotFoundException, CategoryNotFoundException {
            when(storage.fetch(ARTICLE_ID)).thenReturn(Optional.of(getArticle()));
            service.edit(getRequest());
            Article updatedArticle = Article.builder().id(ARTICLE_ID).categoryId(UPDATED_CATEGORY_ID).title(UPDATED_TITLE)
                    .content(UPDATED_CONTENT).description(UPDATED_DESCRIPTION).thumbnailImageUrl(UPDATED_THUMBNAIL_IMAGE_URL)
                    .keywords(List.of(UPDATED_KEYWORD)).version(VERSION).createdDate(CREATED_DATE).lastModifiedDate(LAST_MODIFIED_DATE)
                    .hidden(UPDATED_HIDDEN).estimatedReadingTime(UPDATED_ESTIMATED_READING_TIME).build();
            verify(storage).save(updatedArticle);
        }

        private static ArticleUpdateRequest getRequest() {
            return ArticleUpdateRequest.builder().id(ARTICLE_ID).categoryId(UPDATED_CATEGORY_ID).title(UPDATED_TITLE)
                    .content(UPDATED_CONTENT).description(UPDATED_DESCRIPTION).thumbnailImageUrl(UPDATED_THUMBNAIL_IMAGE_URL)
                    .keywords(List.of(UPDATED_KEYWORD)).hidden(UPDATED_HIDDEN).estimatedReadingTime(UPDATED_ESTIMATED_READING_TIME).build();
        }
    }

    private Article getArticle() {
        return Article.builder().id(ARTICLE_ID).title(TITLE).description(DESCRIPTION).content(CONTENT)
                .categoryId(CATEGORY_ID).thumbnailImageUrl(THUMBNAIL_IMAGE_URL).keywords(List.of(KEYWORD))
                .version(VERSION).createdDate(CREATED_DATE).lastModifiedDate(LAST_MODIFIED_DATE).hidden(true)
                .estimatedReadingTime(ESTIMATED_READING_TIME).build();
    }
}