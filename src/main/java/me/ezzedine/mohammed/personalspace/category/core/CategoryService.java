package me.ezzedine.mohammed.personalspace.category.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermission;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermissionGranter;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryFetcher, CategoryPersister, CategoryDeleter {

    private final CategoryStorage storage;
    private final CategoryNameValidator nameValidator;
    private final CategoryIdGenerator idGenerator;
    private final CategoryOrderResolver orderResolver;
    private final List<CategoryDeletionPermissionGranter> categoryDeletionPermissionGranters;

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
    public void delete(String id) throws CategoryNotFoundException, CategoryDeletionRejectedException {
        validateCategoryExists(id);

        validateThatCategoryCanBeDeleted(id);

        storage.delete(id);
    }

    private void validateThatCategoryCanBeDeleted(String id) throws CategoryDeletionRejectedException {
        Optional<CategoryDeletionPermission> ungrantedPermission = categoryDeletionPermissionGranters.stream()
                .map(granter -> granter.canDeleteCategory(id))
                .filter(permission -> !permission.isAllowed())
                .findAny();

        if (ungrantedPermission.isPresent()) {
            String rejectionReason = ungrantedPermission.get().getMessage().orElse("Category cannot be deleted at the moment.");
            log.info("Category with ID {} cannot be deleted. Reason {}", id, rejectionReason);
            throw new CategoryDeletionRejectedException(rejectionReason);
        }
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
