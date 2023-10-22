package me.ezzedine.mohammed.personalspace.category.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategory;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryFetcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleCategoryController implements ArticleCategoryApi {

    private final ArticleCategoryFetcher fetcher;

    @Override
    public ResponseEntity<List<ArticleCategorySummaryApiModel>> fetchCategoriesSummaries() {
        log.info("Received a request to fetch the list of all category summaries");
        List<ArticleCategorySummaryApiModel> result = fetcher.fetchAll().stream().map(ArticleCategoryController::toApiModel).toList();
        return ResponseEntity.ok(result);
    }

    private static ArticleCategorySummaryApiModel toApiModel(ArticleCategory category) {
        return ArticleCategorySummaryApiModel.builder().id(category.getId()).name(category.getName())
                .canBeDeleted(category.canBeDeleted()).build();
    }
}
