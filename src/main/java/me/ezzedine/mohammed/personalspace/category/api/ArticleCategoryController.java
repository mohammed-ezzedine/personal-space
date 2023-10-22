package me.ezzedine.mohammed.personalspace.category.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.category.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleCategoryController implements ArticleCategoryApi {

    private final ArticleCategoryFetcher fetcher;
    private final ArticleCategoryPersister persister;

    @Override
    public ResponseEntity<List<ArticleCategorySummaryApiModel>> fetchCategoriesSummaries() {
        log.info("Received a request to fetch the list of all category summaries");
        List<ArticleCategorySummaryApiModel> result = fetcher.fetchAll().stream().map(ArticleCategoryController::toApiModel).toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<CategoryCreationResultApiModel> create(CategoryCreationRequest request) throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException {
        log.info("Received a request to create a new category {}", request);
        ArticleCategoryCreationResult creationResult = persister.persist(PersistArticleCategoryRequest.builder().name(request.getName()).build());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryCreationResultApiModel.builder().id(creationResult.getId()).build());
    }

    private static ArticleCategorySummaryApiModel toApiModel(ArticleCategory category) {
        return ArticleCategorySummaryApiModel.builder().id(category.getId()).name(category.getName())
                .canBeDeleted(category.canBeDeleted()).build();
    }
}
