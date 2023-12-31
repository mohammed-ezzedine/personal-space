package me.ezzedine.mohammed.personalspace.article.core.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcreteArticleImagesStoragePathFactoryTest {

    @Test
    @DisplayName("should return a path relative to the root directory")
    void should_return_a_path_relative_to_the_root_directory() {
        assertEquals(Paths.get(System.getProperty("user.dir"), "article-images").toString(), new ConcreteArticleImagesStoragePathFactory().get());
    }

}