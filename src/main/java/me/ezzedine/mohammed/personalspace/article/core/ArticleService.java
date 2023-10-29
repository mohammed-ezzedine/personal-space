package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.CategoryFetcher;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleCreator {

    private final ArticleStorage storage;
    private final CategoryFetcher categoryFetcher;
    private final ArticleIdGenerator idGenerator;

    @Override
    public ArticleCreationResult create(ArticleCreationRequest request) throws CategoryNotFoundException {
        validateCategoryIdExists(request);

        String id = saveArticleInStorage(request);

        return ArticleCreationResult.builder().id(id).build();
    }

    private String saveArticleInStorage(ArticleCreationRequest request) {
        String articleId = idGenerator.generate();
        storage.save(buildArticle(request, articleId));
        return articleId;
    }

    private static Article buildArticle(ArticleCreationRequest request, String articleId) {
        return Article.builder().id(articleId).categoryId(request.getCategoryId()).content(request.getContent())
                .description(request.getDescription()).title(request.getTitle()).build();
    }

    private void validateCategoryIdExists(ArticleCreationRequest request) throws CategoryNotFoundException {
        if (!categoryFetcher.exists(request.getCategoryId())) {
            throw new CategoryNotFoundException(request.getCategoryId());
        }
    }
}
