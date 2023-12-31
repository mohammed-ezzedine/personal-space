package me.ezzedine.mohammed.personalspace.article.api.image;

import com.google.common.jimfs.Jimfs;
import me.ezzedine.mohammed.personalspace.SecurityTestConfiguration;
import me.ezzedine.mohammed.personalspace.article.api.image.advice.FailedToUploadImageAdvice;
import me.ezzedine.mohammed.personalspace.article.api.image.advice.ImageDoesNotExistAdvice;
import me.ezzedine.mohammed.personalspace.article.api.image.advice.ImageNameAlreadyExistsAdvice;
import me.ezzedine.mohammed.personalspace.article.core.image.ArticleImageService;
import me.ezzedine.mohammed.personalspace.article.core.image.FailedToUploadImageException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageDoesNotExistException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageNameAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.ezzedine.mohammed.personalspace.TestUtils.loadResource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        ArticleImageController.class,
        FailedToUploadImageAdvice.class,
        ImageNameAlreadyExistsAdvice.class,
        ImageDoesNotExistAdvice.class
})
@Import(SecurityTestConfiguration.class)
@EnableWebMvc
@AutoConfigureMockMvc(addFilters = false)
class ArticleImageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleImageService imageService;

    @Nested
    @DisplayName("When uploading an image")
    class UploadingImageIntegrationTest {

        @Test
        @WithAnonymousUser
        @DisplayName("non authenticated users cannot upload images")
        void non_authenticated_users_cannot_upload_images() {
            assertThrows(Exception.class, () -> mockMvc.perform(multipart("/api/articles/images").file(getMockImage())));
        }

        @Test
        @WithMockUser
        @DisplayName("authenticated users that are not admins cannot upload images")
        void authenticated_users_that_are_not_admins_cannot_upload_images() {
            assertThrows(Exception.class, () -> mockMvc.perform(multipart("/api/articles/images").file(getMockImage())));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("admin users can upload images")
        void admin_users_can_upload_images() {
            assertDoesNotThrow(() -> mockMvc.perform(multipart("/api/articles/images").file(getMockImage())));
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a conflict status code if the image name already exists")
        void should_return_a_conflict_status_code_if_the_image_name_already_exists() throws Exception {
            MockMultipartFile mockImage = getMockImage();

            doThrow(ImageNameAlreadyExistsException.class).when(imageService).upload(any(), any());
            mockMvc.perform(multipart("/api/articles/images").file(mockImage))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("should return a server error if an exception happens while uploading the image")
        void should_return_a_server_error_if_an_exception_happens_while_uploading_the_image() throws Exception {
            MockMultipartFile mockImage = getMockImage();

            doThrow(FailedToUploadImageException.class).when(imageService).upload(any(), any());
            mockMvc.perform(multipart("/api/articles/images").file(mockImage))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser(authorities = "admin")
        @DisplayName("the user should get the link of the uploaded image in the response")
        void the_user_should_get_the_link_of_the_uploaded_image_in_the_response() throws Exception {
            MockMultipartFile mockImage = getMockImage();
            String contentAsString = mockMvc.perform(multipart("/api/articles/images").file(mockImage))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(loadResource("article/api/upload_image_response.json"), contentAsString);
        }

        private static MockMultipartFile getMockImage() {
            return new MockMultipartFile("upload", "imageName.jpg", "text/plain", "imageContent".getBytes());
        }
    }

    @Nested
    @DisplayName("When serving an image")
    class ServingImageIntegrationTest {

        @Test
        @DisplayName("should return a not found status when the image does not exist")
        void should_return_a_not_found_status_when_the_image_does_not_exist() throws Exception {
            when(imageService.serveImage("imageName")).thenThrow(ImageDoesNotExistException.class);

            mockMvc.perform(get("/api/articles/images/imageName"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return the image resource when it exists")
        void should_return_the_image_resource_when_it_exists() throws Exception {
            FileSystem fileSystem = Jimfs.newFileSystem();
            Path path = fileSystem.getPath("imagePath");
            Files.write(path, "imageContent".getBytes());

            when(imageService.serveImage("imageName")).thenReturn(new UrlResource(path.toUri()));
            String contentAsString = mockMvc.perform(get("/api/articles/images/imageName"))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse().getContentAsString();
            assertEquals("imageContent", contentAsString);
        }
    }
}