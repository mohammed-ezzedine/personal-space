package me.ezzedine.mohammed.personalspace.article.core.image;

import org.springframework.core.io.Resource;

public interface ArticleImageService {
    void upload(String name, byte[] content) throws ImageNameAlreadyExistsException, FailedToUploadImageException;
    Resource serveImage(String name) throws ImageDoesNotExistException;
}
