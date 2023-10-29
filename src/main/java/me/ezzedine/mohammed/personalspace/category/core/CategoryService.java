package me.ezzedine.mohammed.personalspace.category.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryFetcher, CategoryPersister {

    private final CategoryStorage storage;
    private final CategoryNameValidator nameValidator;
    private final CategoryIdGenerator idGenerator;
    private final CategoryOrderResolver orderResolver;

    @Override
    public List<Category> fetchAll() {
        return storage.fetchAllOrderedByOrder();
    }

    @Override
    public Category fetch(String id) throws CategoryNotFoundException {
        return storage.fetch(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public CategoryCreationResult persist(PersistCategoryRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
        validateCategoryName(request);

        String categoryId = idGenerator.generate(request.getName());
        validateIdIsNotTaken(request, categoryId);

        int order = orderResolver.resolveOrderForNewCategory();

        storage.persist(Category.builder().id(categoryId).name(request.getName()).order(order).build());

        return CategoryCreationResult.builder().id(categoryId).order(order).build();
    }

    @Override
    public void updateCategoriesOrders(UpdateCategoriesOrdersRequest request) {
        request.getCategoryOrders().forEach(this::updateCategoryOrder);
    }

    @Override
    public void delete(String id) throws CategoryNotFoundException {
        validateCategoryExists(id);
        storage.delete(id);
    }

    private void updateCategoryOrder(UpdateCategoriesOrdersRequest.CategoryOrder categoryOrder) {
        Optional<Category> category = storage.fetch(categoryOrder.getCategoryId());
        category.ifPresent(c -> {
            c.setOrder(categoryOrder.getOrder());
            storage.persist(c);
        });
    }

    private void validateCategoryExists(String id) throws CategoryNotFoundException {
        Optional<Category> optionalCategory = storage.fetch(id);
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException(id);
        }
    }

    private void validateIdIsNotTaken(PersistCategoryRequest request, String categoryId) throws CategoryIdAlreadyExistsException {
        if (storage.categoryExists(categoryId)) {
            throw new CategoryIdAlreadyExistsException(request.getName());
        }
    }

    private void validateCategoryName(PersistCategoryRequest request) throws CategoryValidationViolationException {
        CategoryNameValidationResult result = nameValidator.validate(request.getName());
        if (!result.isValid()) {
            throw new CategoryValidationViolationException(result.getViolations().stream().map(CategoryNameViolation::getMessage).toList());
        }
    }
}
