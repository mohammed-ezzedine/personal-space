package me.ezzedine.mohammed.personalspace.article.infra;

import me.ezzedine.mohammed.personalspace.DatabaseIntegrationTest;
import me.ezzedine.mohammed.personalspace.MongoConfiguration;
import me.ezzedine.mohammed.personalspace.util.sequence.SerialNumberSequence;
import me.ezzedine.mohammed.personalspace.util.sequence.SerialNumberSequenceMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static me.ezzedine.mohammed.personalspace.article.infra.ArticleSerialNumberMongoProvider.ARTICLE_SERIAL_NUMBER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        SerialNumberSequenceMongoRepository.class,
        ArticleSerialNumberMongoProvider.class,
        MongoConfiguration.class
})
class ArticleSerialNumberMongoProviderIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    private SerialNumberSequenceMongoRepository repository;

    @Autowired
    private ArticleSerialNumberMongoProvider provider;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("it should return one when there is no record of an article serial number in the storage")
    void it_should_return_one_when_there_is_no_record_of_an_article_serial_number_in_the_storage() {
        assertEquals(1L, provider.generateSequenceNumber());
    }

    @Test
    @DisplayName("it should return the next available article serial number stored in the storage when available")
    void it_should_return_the_next_available_article_serial_number_stored_in_the_storage_when_available() {
        repository.save(SerialNumberSequence.builder().id(ARTICLE_SERIAL_NUMBER_ID).sequence(5L).build());
        assertEquals(5L, provider.generateSequenceNumber());
    }

    @Test
    @DisplayName("it should increment the next available article serial number in the storage after using it")
    void it_should_increment_the_next_available_article_serial_number_in_the_storage_after_using_it() {
        repository.save(SerialNumberSequence.builder().id(ARTICLE_SERIAL_NUMBER_ID).sequence(5L).build());
        provider.generateSequenceNumber();
        Optional<SerialNumberSequence> numberSequence = repository.findById(ARTICLE_SERIAL_NUMBER_ID);
        assertTrue(numberSequence.isPresent());
        assertEquals(6L, numberSequence.get().getSequence());
    }
}