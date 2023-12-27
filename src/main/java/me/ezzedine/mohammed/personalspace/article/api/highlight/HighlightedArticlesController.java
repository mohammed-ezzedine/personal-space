package me.ezzedine.mohammed.personalspace.article.api.highlight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.api.ArticleApiMapper;
import me.ezzedine.mohammed.personalspace.article.api.ArticleSummaryApiModel;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HighlightedArticlesController implements HighlightedArticlesApi {

    private final ArticlesHighlightFetcher fetcher;
    private final ArticlesHighlightUpdater updater;

    @Override
    public ResponseEntity<Void> addArticleToHighlights(String id) throws ArticleNotFoundException, ArticleAlreadyHighlightedException {
        log.info("Received a request to add the article {} to the highlights", id);
        updater.addArticleToHighlights(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removeArticleFromHighlights(String id) throws ArticleNotFoundException, ArticleWasNotHighlightedException {
        log.info("Received a request to remove the article {} from the highlights", id);
        updater.removeArticleFromHighlights(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateHighlightedArticles(HighlightedArticlesUpdateRequest request) {
        log.info("Received a request to update the article highlights as follows {}", request);
        updater.updateArticlesHighlights(request.getArticles().stream().map(HighlightedArticlesController::fromApiModel).toList());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ArticleSummaryApiModel>> getHighlightedArticles() {
        log.info("Received a request to fetch the list of highlighted articles");
        List<ArticleSummaryApiModel> highlightedArticles = fetcher.getHighlightedArticles().stream().map(ArticleApiMapper::toSummaryApiModel).toList();
        return ResponseEntity.ok(highlightedArticles);
    }

    @Override
    public ResponseEntity<List<ArticleHighlightApiModel>> getHighlightedArticlesSummary() {
        log.info("Received a request to fetch the summary of the list of highlighted articles");
        List<ArticleHighlightApiModel> summary = fetcher.getHighlightedArticlesSummary().stream().map(HighlightedArticlesController::toApiModel).toList();
        return ResponseEntity.ok(summary);
    }

    private static HighlightedArticle fromApiModel(ArticleHighlightApiModel a) {
        return HighlightedArticle.builder().articleId(a.getArticleId()).highlightRank(a.getRank()).build();
    }

    private static ArticleHighlightApiModel toApiModel(HighlightedArticle a) {
        return ArticleHighlightApiModel.builder().articleId(a.getArticleId()).rank(a.getHighlightRank()).build();
    }
}
