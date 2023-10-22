package me.ezzedine.mohammed.personalspace.category.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCategoryService implements ArticleCategoryFetcher, ArticleCategoryPersister {

    private final ArticleCategoryStorage storage;
    private final ArticleCategoryNameValidator nameValidator;
    private final ArticleCategoryIdGenerator idGenerator;

    @Override
    public List<ArticleCategory> fetchAll() {
        return storage.fetchAll();
    }

    @Override
    public ArticleCategoryCreationResult persist(PersistArticleCategoryRequest request) throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException {
        validateCategoryName(request);

        String categoryId = idGenerator.generate(request.getName());
        validateIdIsNotTaken(request, categoryId);

        storage.persist(ArticleCategory.builder().id(categoryId).name(request.getName()).canBeDeleted(true).build());

        return ArticleCategoryCreationResult.builder().id(categoryId).build();
    }

    @Override
    public void delete(String id) throws ArticleCategoryNotFoundException {
        validateIdExists(id);

        storage.delete(id);
    }

    private void validateIdExists(String id) throws ArticleCategoryNotFoundException {
        if (!storage.categoryExists(id)) {
            throw new ArticleCategoryNotFoundException(id);
        }
    }

    private void validateIdIsNotTaken(PersistArticleCategoryRequest request, String categoryId) throws ArticleCategoryIdAlreadyExistsException {
        if (storage.categoryExists(categoryId)) {
            throw new ArticleCategoryIdAlreadyExistsException(request.getName());
        }
    }

    private void validateCategoryName(PersistArticleCategoryRequest request) throws ArticleCategoryValidationViolationException {
        ArticleCategoryNameValidationResult result = nameValidator.validate(request.getName());
        if (!result.isValid()) {
            throw new ArticleCategoryValidationViolationException(result.getViolations().stream().map(ArticleCategoryNameViolation::getMessage).toList());
        }
    }
}
