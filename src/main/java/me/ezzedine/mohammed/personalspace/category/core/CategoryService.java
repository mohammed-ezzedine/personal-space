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

    @Override
    public List<Category> fetchAll() {
        return storage.fetchAll();
    }

    @Override
    public CategoryCreationResult persist(PersistCategoryRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
        validateCategoryName(request);

        String categoryId = idGenerator.generate(request.getName());
        validateIdIsNotTaken(request, categoryId);

        storage.persist(Category.builder().id(categoryId).name(request.getName()).canBeDeleted(true).build());

        return CategoryCreationResult.builder().id(categoryId).build();
    }

    @Override
    public void delete(String id) throws CategoryNotFoundException, CannotDeleteCategoryException {
        Category category = validateCategoryExists(id);

        if (!category.canBeDeleted()) {
            throw new CannotDeleteCategoryException(id);
        }

        storage.delete(id);
    }

    private Category validateCategoryExists(String id) throws CategoryNotFoundException {
        Optional<Category> optionalCategory = storage.fetch(id);
        if (optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException(id);
        }

        return optionalCategory.get();
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
