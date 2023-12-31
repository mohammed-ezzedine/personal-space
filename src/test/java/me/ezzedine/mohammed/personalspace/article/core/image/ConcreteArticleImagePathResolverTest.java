package me.ezzedine.mohammed.personalspace.article.core.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConcreteArticleImagePathResolverTest {

    private ArticleImagesStoragePathFactory storagePathFactory;
    private ConcreteArticleImagePathResolver imagePathResolver;

    @BeforeEach
    void setUp() {
        storagePathFactory = mock(ArticleImagesStoragePathFactory.class);
        imagePathResolver = new ConcreteArticleImagePathResolver(storagePathFactory);
    }

    @Test
    @DisplayName("should return a path inside the images storage path")
    void should_return_a_path_inside_the_images_storage_path() {
        String storagePath = UUID.randomUUID().toString();
        when(storagePathFactory.get()).thenReturn(storagePath);
        String imageName = UUID.randomUUID().toString();
        Path imagePath = imagePathResolver.resolve(imageName);
        assertEquals(Paths.get(storagePath, imageName), imagePath);
    }
}