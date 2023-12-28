package me.ezzedine.mohammed.personalspace.article.core;

import me.ezzedine.mohammed.personalspace.util.hash.HashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArticleHashedIdGeneratorTest {

    private ArticleSerialNumberProvider serialNumberProvider;
    private HashingService hashingService;
    private ArticleHashedIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        serialNumberProvider = mock(ArticleSerialNumberProvider.class);
        hashingService = mock(HashingService.class);
        idGenerator = new ArticleHashedIdGenerator(serialNumberProvider, hashingService);
    }

    @Test
    @DisplayName("it should provide hashed sequential numbers as article ids")
    void it_should_provide_hashed_sequential_numbers_as_article_ids() {
        long serialNumber = new Random().nextLong();
        when(serialNumberProvider.generateSequenceNumber()).thenReturn(serialNumber);
        String hashedId = UUID.randomUUID().toString();
        when(hashingService.hash(serialNumber)).thenReturn(hashedId);
        assertEquals(hashedId, idGenerator.generate());
    }
}