package me.ezzedine.mohammed.personalspace.article.api.image;

import me.ezzedine.mohammed.personalspace.article.core.image.FailedToUploadImageException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageDoesNotExistException;
import me.ezzedine.mohammed.personalspace.article.core.image.ImageNameAlreadyExistsException;
import me.ezzedine.mohammed.personalspace.config.security.AdminAccess;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("api/articles/images")
public interface ArticleImageApi {

    @PostMapping
    @AdminAccess
    ResponseEntity<UploadImageApiResponse> uploadImage(@RequestParam("upload") MultipartFile image) throws ImageNameAlreadyExistsException, FailedToUploadImageException;

    @GetMapping("{name}")
    ResponseEntity<Resource> serveImage(@PathVariable String name) throws ImageDoesNotExistException;
}
