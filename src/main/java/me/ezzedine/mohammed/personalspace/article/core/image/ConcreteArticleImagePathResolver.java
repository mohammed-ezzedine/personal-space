package me.ezzedine.mohammed.personalspace.article.core.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ConcreteArticleImagePathResolver implements ArticleImagePathResolver {

    private final ArticleImagesStoragePathFactory storagePathFactory;

    @Override
    public Path resolve(String imageName) {
        return Paths.get(storagePathFactory.get(), imageName);
    }
}
