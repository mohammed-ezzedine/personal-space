package me.ezzedine.mohammed.personalspace.article.core.image;

import java.nio.file.Path;

public interface ArticleImagePathResolver {
    Path resolve(String imageName);
}
