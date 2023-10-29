package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.Category;
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
        Category category = categoryFetcher.fetch(request.getCategoryId());

        String id = saveArticleInStorage(request, category);

        return ArticleCreationResult.builder().id(id).build();
    }

    private String saveArticleInStorage(ArticleCreationRequest request, Category category) {
        String articleId = idGenerator.generate();
        Article article = Article.builder().id(articleId).content(request.getContent()).category(category)
                .description(request.getDescription()).title(request.getTitle()).build();
        storage.save(article);
        return articleId;
    }

}
