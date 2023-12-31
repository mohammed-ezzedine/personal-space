package me.ezzedine.mohammed.personalspace.article.core.image;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConcreteArticleImageServiceTest {

    public static final String IMAGE_NAME = "image.jpg";
    public static final String IMAGE_PATH = Paths.get("parent", IMAGE_NAME).toString() ;
    private ConcreteArticleImageService service;
    private Path path;

    @BeforeEach
    void setUp() {
        ArticleImagePathResolver pathResolver = mock(ArticleImagePathResolver.class);
        service = new ConcreteArticleImageService(pathResolver);

        FileSystem fileSystem = Jimfs.newFileSystem();

        path = fileSystem.getPath(IMAGE_PATH);
        when(pathResolver.resolve(IMAGE_NAME)).thenReturn(path);
    }

    @Nested
    @DisplayName("When uploading an image")
    class UploadingImageTest {
        @Test
        @DisplayName("it should fail to upload an image if another image with the same name already exists")
        void it_should_fail_to_upload_an_image_if_another_image_with_the_same_name_already_exists() throws IOException {
            Files.createDirectories(path.getParent());
            Files.write(path, "anything".getBytes());

            assertThrows(ImageNameAlreadyExistsException.class, () -> service.upload(IMAGE_NAME, "image content".getBytes()));
        }

        @Test
        @DisplayName("it should upload the image to the target path on the happy path")
        void it_should_upload_the_image_to_the_target_path_on_the_happy_path() throws IOException, ImageNameAlreadyExistsException, FailedToUploadImageException {
            service.upload(IMAGE_NAME, "image content".getBytes());
            assertEquals(List.of("image content"), Files.readAllLines(path));
        }

        @Test
        @DisplayName("wraps any IO failure with a domain exception")
        void wraps_any_IO_failure_with_a_domain_exception() throws IOException {
            Files.write(path.getParent(), UUID.randomUUID().toString().getBytes());
            assertThrows(FailedToUploadImageException.class, () -> service.upload(IMAGE_NAME, UUID.randomUUID().toString().getBytes()));
        }
    }

    @Nested
    @DisplayName("When serving an image")
    class ServingImageTest {
        @Test
        @DisplayName("it should fail if the image does not exist")
        void it_should_fail_if_the_image_does_not_exist() {
            assertThrows(ImageDoesNotExistException.class, () -> service.serveImage(IMAGE_NAME));
        }

        @Test
        @DisplayName("should return the image as a url resource when it exists")
        void should_return_the_image_as_a_url_resource_when_it_exists() throws IOException, ImageDoesNotExistException {
            Files.createDirectories(path.getParent());
            Files.write(path, "image content".getBytes());
            Resource resource = service.serveImage(IMAGE_NAME);
            assertEquals("image content", resource.getContentAsString(Charset.defaultCharset()));
        }
    }
}