package me.ezzedine.mohammed.personalspace.article.core.image;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConcreteArticleImagesStoragePathFactory implements ArticleImagesStoragePathFactory {

    @Override
    public String get() {
        Path path = Paths.get(System.getProperty("user.dir"), "article-images");
        return path.toString();
    }
}
