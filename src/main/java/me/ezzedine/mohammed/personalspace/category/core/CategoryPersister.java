package me.ezzedine.mohammed.personalspace.category.core;

public interface CategoryPersister {
    CategoryCreationResult persist(PersistCategoryRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException;
    void updateCategoriesOrders(UpdateCategoriesOrdersRequest request);
}
