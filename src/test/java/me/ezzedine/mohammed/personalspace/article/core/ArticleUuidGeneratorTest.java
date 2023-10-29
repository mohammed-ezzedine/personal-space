package me.ezzedine.mohammed.personalspace.article.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArticleUuidGeneratorTest {

    private ArticleUuidGenerator articleUuidGenerator;

    @BeforeEach
    void setUp() {
        articleUuidGenerator = new ArticleUuidGenerator();
    }

    @Test
    @DisplayName("should generate a non null string")
    void should_generate_a_non_null_string() {
        assertNotNull(articleUuidGenerator.generate());
    }

    @Test
    @DisplayName("should generate a uuid string")
    void should_generate_a_uuid_string() {
        assertDoesNotThrow(() -> UUID.fromString(articleUuidGenerator.generate()));
    }
}