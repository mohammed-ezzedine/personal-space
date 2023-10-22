package me.ezzedine.mohammed.personalspace;

import me.ezzedine.mohammed.personalspace.category.api.ArticleCategoryController;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtils {
    public static String getResource(String resourcePath) throws IOException {
        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            String content = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
            return content.replaceAll("\\s+", "");
        }
    }
}
