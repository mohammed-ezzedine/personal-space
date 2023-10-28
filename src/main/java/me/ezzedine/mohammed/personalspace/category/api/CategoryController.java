package me.ezzedine.mohammed.personalspace.category.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.category.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryFetcher fetcher;
    private final CategoryPersister persister;

    @Override
    public ResponseEntity<List<CategorySummaryApiModel>> fetchCategoriesSummaries() {
        log.info("Received a request to fetch the list of all category summaries");
        List<CategorySummaryApiModel> result = fetcher.fetchAll().stream().map(CategoryController::toApiModel).toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<CategoryCreationResultApiModel> create(CategoryCreationRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException {
        log.info("Received a request to create a new category {}", request);
        CategoryCreationResult creationResult = persister.persist(PersistCategoryRequest.builder().name(request.getName()).build());
        CategoryCreationResultApiModel result = CategoryCreationResultApiModel.builder().id(creationResult.getId()).order(creationResult.getOrder()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private static CategorySummaryApiModel toApiModel(Category category) {
        return CategorySummaryApiModel.builder().id(category.getId()).name(category.getName()).order(category.getOrder())
                .canBeDeleted(category.canBeDeleted()).build();
    }
}
