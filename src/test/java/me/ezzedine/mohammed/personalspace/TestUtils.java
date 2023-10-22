package me.ezzedine.mohammed.personalspace;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtils {
    public static String loadResource(String resourcePath) throws IOException {
        return loadResourceWithWhiteSpaces(resourcePath).replaceAll("\\s+", "");
    }
    public static String loadResourceWithWhiteSpaces(String resourcePath) throws IOException {
        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
        }
    }
}
