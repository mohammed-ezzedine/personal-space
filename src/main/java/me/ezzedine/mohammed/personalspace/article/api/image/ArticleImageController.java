package me.ezzedine.mohammed.personalspace.article.api.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.core.image.ArticleImageService;
import me.ezzedine.mohammed.personalspace.article.core.image.FailedToUploadImageException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageDoesNotExistException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageNameAlreadyExistsException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleImageController implements ArticleImageApi {

    private final ArticleImageService imageService;

    @Override
    public ResponseEntity<UploadImageApiResponse> uploadImage(MultipartFile image) throws ImageNameAlreadyExistsException, FailedToUploadImageException {
        log.info("Received a request to upload the image {}", image.getOriginalFilename());
        try {
            imageService.upload(image.getOriginalFilename(), image.getBytes());
        } catch (IOException e) {
            throw new FailedToUploadImageException(e.getMessage());
        }

        UploadImageApiResponse response = UploadImageApiResponse.builder().url(getImageLink(image)).build();
        return ResponseEntity.ok().body(response);
    }

    @SneakyThrows
    private static String getImageLink(MultipartFile image) {
        return linkTo(methodOn(ArticleImageController.class).serveImage(image.getOriginalFilename())).toUri().toString();
    }

    @Override
    public ResponseEntity<Resource> serveImage(String name) throws ImageDoesNotExistException {
        log.info("Received a request to serve the image {}", name);
        Resource resource = imageService.serveImage(name);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", resource.getFilename()))
                .body(resource);
    }
}
