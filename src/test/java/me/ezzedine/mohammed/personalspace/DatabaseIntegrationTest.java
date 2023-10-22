package me.ezzedine.mohammed.personalspace;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DatabaseIntegrationTest {

    private static final MongoDbContainer container = new MongoDbContainer();

    @BeforeAll
    static void beforeAll() {
        System.out.println("starting");
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }
}
