package me.ezzedine.mohammed.personalspace.category.core;

public interface ArticleCategoryPersister {
    ArticleCategoryCreationResult persist(PersistArticleCategoryRequest request) throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException;
    void delete(String id) throws ArticleCategoryNotFoundException, CannotDeleteArticleCategoryException;
}
