package me.ezzedine.mohammed.personalspace.article.core.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ConcreteArticleImageService implements ArticleImageService {

    private final ArticleImagePathResolver pathResolver;

    @Override
    public void upload(String name, byte[] content) throws ImageNameAlreadyExistsException, FailedToUploadImageException {
        Path imagePath = pathResolver.resolve(name);

        if (Files.exists(imagePath)) {
            throw new ImageNameAlreadyExistsException(name);
        }

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, content);
        } catch (IOException e) {
            throw new FailedToUploadImageException(e.getMessage());
        }
    }

    @Override
    @SneakyThrows(MalformedURLException.class)
    public Resource serveImage(String name) throws ImageDoesNotExistException {
        Path imagePath = pathResolver.resolve(name);
        if (Files.notExists(imagePath)) {
            throw new ImageDoesNotExistException(name);
        }

        return new UrlResource(imagePath.toUri());
    }
}
