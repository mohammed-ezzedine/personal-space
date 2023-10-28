package me.ezzedine.mohammed.personalspace.category.core;

public interface CategoryPersister {
    CategoryCreationResult persist(PersistCategoryRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException;
    void delete(String id) throws CategoryNotFoundException, CannotDeleteCategoryException;
}
