package me.ezzedine.mohammed.personalspace.util.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = HashingConfig.class, properties = { "article.id.hash.salt=abc", "article.id.hash.size=3" })
@EnableConfigurationProperties
class HashingConfigIntegrationTest {

    @Autowired
    private HashingConfig config;

    @Test
    @DisplayName("reads the values correctly from the properties")
    void reads_the_values_correctly_from_the_properties() {
        assertEquals("abc", config.getSalt());
        assertEquals(3, config.getSize());
    }
}