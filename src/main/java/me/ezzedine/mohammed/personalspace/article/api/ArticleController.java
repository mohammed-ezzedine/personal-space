package me.ezzedine.mohammed.personalspace.article.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.core.*;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {

    private final ArticleCreator articleCreator;
    private final ArticleFetcher articleFetcher;
    private final ArticleEditor articleEditor;

    @Override
    public ResponseEntity<List<ArticleApiModel>> getArticles() {
        log.info("Received a request to fetch the article details");
        List<Article> articles = articleFetcher.fetchAll();
        List<ArticleApiModel> response = articles.stream().map(ArticleController::toApiModel).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArticleApiModel> getArticle(String id) throws ArticleNotFoundException {
        log.info("Received a request to fetch the details of article with id={}", id);
        Article article = articleFetcher.fetch(id);
        return ResponseEntity.ok(toApiModel(article));
    }

    @Override
    public ResponseEntity<ArticleCreationApiResponse> create(ArticleCreationApiRequest request) throws CategoryNotFoundException {
        log.info("Received a request to create an article");
        ArticleCreationResult result = articleCreator.create(toDomainModel(request));
        ArticleCreationApiResponse response = ArticleCreationApiResponse.builder().id(result.getId()).build();
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    @Override
    public ResponseEntity<Void> editArticle(String id, ArticleUpdateApiRequest request) throws CategoryNotFoundException, ArticleNotFoundException {
        log.info("Received a request to edit article with ID {}", id);
        articleEditor.edit(toDomainModel(id, request));
        return ResponseEntity.ok().build();
    }

    private static ArticleUpdateRequest toDomainModel(String id, ArticleUpdateApiRequest request) {
        return ArticleUpdateRequest.builder().id(id).title(request.getTitle()).description(request.getDescription())
                .content(request.getContent()).categoryId(request.getCategoryId()).thumbnailImageUrl(request.getThumbnailImageUrl())
                .keywords(request.getKeywords()).build();
    }

    private static ArticleApiModel toApiModel(Article article) {
        return ArticleApiModel.builder().id(article.getId()).title(article.getTitle()).description(article.getDescription())
                .content(article.getContent()).categoryId(article.getCategoryId()).thumbnailImageUrl(article.getThumbnailImageUrl())
                .keywords(article.getKeywords()).build();
    }

    private static ArticleCreationRequest toDomainModel(ArticleCreationApiRequest request) {
        return ArticleCreationRequest.builder().title(request.getTitle()).content(request.getContent())
                .description(request.getDescription()).categoryId(request.getCategoryId())
                .thumbnailImageUrl(request.getThumbnailImageUrl()).keywords(request.getKeywords()).build();
    }
}
