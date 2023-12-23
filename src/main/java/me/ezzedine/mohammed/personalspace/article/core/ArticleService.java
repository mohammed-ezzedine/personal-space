package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.CategoryFetcher;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleCreator, ArticleFetcher, ArticleEditor {

    private final ArticleStorage storage;
    private final CategoryFetcher categoryFetcher;
    private final ArticleIdGenerator idGenerator;

    @Override
    public List<Article> fetchAll() {
        return storage.fetchAll();
    }

    @Override
    public Article fetch(String id) throws ArticleNotFoundException {
        return storage.fetch(id).orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Override
    public ArticleCreationResult create(ArticleCreationRequest request) throws CategoryNotFoundException {
        validateCategoryExists(request.getCategoryId());

        String id = saveArticleInStorage(request);

        return ArticleCreationResult.builder().id(id).build();
    }

    @Override
    public void edit(ArticleUpdateRequest request) throws ArticleNotFoundException, CategoryNotFoundException {
        Optional<String> optionalCategoryId = request.getCategoryId();
        if(optionalCategoryId.isPresent()) {
            validateCategoryExists(optionalCategoryId.get());
        };

        Article article = storage.fetch(request.getId()).orElseThrow(() -> new ArticleNotFoundException(request.getId()));
        request.getTitle().ifPresent(article::setTitle);
        request.getDescription().ifPresent(article::setDescription);
        request.getCategoryId().ifPresent(article::setCategoryId);
        request.getContent().ifPresent(article::setContent);

        storage.save(article);
    }

    private void validateCategoryExists(String categoryId) throws CategoryNotFoundException {
        categoryFetcher.fetch(categoryId);
    }

    private String saveArticleInStorage(ArticleCreationRequest request) {
        String articleId = idGenerator.generate();
        Article article = Article.builder().id(articleId).content(request.getContent()).categoryId(request.getCategoryId())
                .description(request.getDescription()).title(request.getTitle()).build();
        storage.save(article);
        return articleId;
    }
}
