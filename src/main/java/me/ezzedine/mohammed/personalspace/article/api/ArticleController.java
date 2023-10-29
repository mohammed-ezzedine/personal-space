package me.ezzedine.mohammed.personalspace.article.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.core.ArticleCreationRequest;
import me.ezzedine.mohammed.personalspace.article.core.ArticleCreationResult;
import me.ezzedine.mohammed.personalspace.article.core.ArticleCreator;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {

    private final ArticleCreator articleCreator;

    @Override
    public ResponseEntity<ArticleCreationApiResponse> create(ArticleCreationApiRequest request) throws CategoryNotFoundException {
        log.info("Received a request to create an article");
        ArticleCreationResult result = articleCreator.create(toDomainModel(request));
        ArticleCreationApiResponse response = ArticleCreationApiResponse.builder().id(result.getId()).build();
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    private static ArticleCreationRequest toDomainModel(ArticleCreationApiRequest request) {
        return ArticleCreationRequest.builder().title(request.getTitle()).content(request.getContent())
                .description(request.getDescription()).categoryId(request.getCategoryId()).build();
    }
}
