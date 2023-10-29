package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.CategoryFetcher;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleCreator, ArticleFetcher {

    private final ArticleStorage storage;
    private final CategoryFetcher categoryFetcher;
    private final ArticleIdGenerator idGenerator;

    @Override
    public ArticleCreationResult create(ArticleCreationRequest request) throws CategoryNotFoundException {
       validateCategoryExists(request);

        String id = saveArticleInStorage(request);

        return ArticleCreationResult.builder().id(id).build();
    }

    private void validateCategoryExists(ArticleCreationRequest request) throws CategoryNotFoundException {
        categoryFetcher.fetch(request.getCategoryId());
    }

    private String saveArticleInStorage(ArticleCreationRequest request) {
        String articleId = idGenerator.generate();
        Article article = Article.builder().id(articleId).content(request.getContent()).categoryId(request.getCategoryId())
                .description(request.getDescription()).title(request.getTitle()).build();
        storage.save(article);
        return articleId;
    }

    @Override
    public Article fetch(String id) throws ArticleNotFoundException {
        return storage.fetch(id).orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Override
    public List<Article> fetchAll() {
        return storage.fetchAll();
    }
}
