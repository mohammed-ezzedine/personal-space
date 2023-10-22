package me.ezzedine.mohammed.personalspace;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.RemoteDockerImage;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

    private static final String IMAGE_NAME = "mongo:latest";

    public MongoDbContainer() {
        super(IMAGE_NAME);

        this.withExposedPorts(27017);
        this.withEnv("MONGO_INITDB_ROOT_USERNAME", "admin");
        this.withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin");
    }

    @Override
    public void start() {
        super.start();

        System.setProperty("MONGO_DB_PORT", getMappedPort(27017).toString());
    }

}
