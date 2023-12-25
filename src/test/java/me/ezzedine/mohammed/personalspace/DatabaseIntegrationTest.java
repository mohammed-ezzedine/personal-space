package me.ezzedine.mohammed.personalspace;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnableAutoConfiguration
@AutoConfigureDataMongo
@EnableMongoRepositories
public class DatabaseIntegrationTest {

    private static final MongoDbContainer container = new MongoDbContainer();

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }
}
