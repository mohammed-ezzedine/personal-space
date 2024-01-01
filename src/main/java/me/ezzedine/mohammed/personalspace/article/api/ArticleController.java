package me.ezzedine.mohammed.personalspace.article.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.core.*;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import me.ezzedine.mohammed.personalspace.util.pagination.PaginationCriteria;
import me.ezzedine.mohammed.personalspace.util.sort.SortingCriteria;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.ezzedine.mohammed.personalspace.article.api.ArticleApiMapper.toApiModel;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ArticleController implements ArticleApi {

    private final ArticleCreator articleCreator;
    private final ArticleFetcher articleFetcher;
    private final ArticleEditor articleEditor;

    @Override
    public ResponseEntity<Page<ArticleSummaryApiModel>> getArticles(ArticlesFetchApiCriteria fetchCriteria, Principal principal) {
        log.info("Received a request to fetch the article details with fetch criteria {}", fetchCriteria);
        ArticlesFetchCriteria domainFetchCriteria = getFetchCriteria(fetchCriteria, principal);
        Page<Article> articlesPage = articleFetcher.fetchAll(domainFetchCriteria);
        List<ArticleSummaryApiModel> articlesApiModels = articlesPage.getItems().stream()
                .map(ArticleApiMapper::toSummaryApiModel).collect(Collectors.toList());
        Page<ArticleSummaryApiModel> articlesApiModelPage = Page.<ArticleSummaryApiModel>builder().totalSize(articlesPage.getTotalSize()).items(articlesApiModels).build();
        return ResponseEntity.ok(articlesApiModelPage);
    }

    @Override
    public ResponseEntity<ArticleApiModel> getArticle(String id, Principal principal) throws ArticleNotFoundException {
        log.info("Received a request to fetch the details of article with id={}", id);
        Article article = articleFetcher.fetch(id);

        if (article.isHidden() && !isAdmin(principal)) {
            throw new ArticleNotFoundException(id);
        }

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

    private static boolean isAdmin(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return Optional.ofNullable(token).map(u -> u.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"))).orElse(false);
    }

    private static ArticlesFetchCriteria getFetchCriteria(ArticlesFetchApiCriteria fetchCriteria, Principal principal) {
        ArticlesFetchCriteria criteria = ArticlesFetchCriteria.builder().highlighted(fetchCriteria.getHighlighted())
                .categoryId(fetchCriteria.getCategoryId()).build();

        if (fetchCriteria.getPage().isPresent()) {
            PaginationCriteria paginationCriteria = PaginationCriteria.builder().startingPageIndex(fetchCriteria.getPage().get()).maximumPageSize(fetchCriteria.getSize().orElse(10)).build();
            criteria.setPaginationCriteria(paginationCriteria);
        }

        if (fetchCriteria.getSortBy().isPresent()) {
            SortingCriteria sortingCriteria = SortingCriteria.builder().field(fetchCriteria.getSortBy().get()).ascendingOrder(fetchCriteria.getAscOrder().orElse(true)).build();
            criteria.setSortingCriteria(sortingCriteria);
        }


        if (!isAdmin(principal)) {
            // TODO add test
            criteria.setHidden(false);
        }

        return criteria;
    }

    private static ArticleUpdateRequest toDomainModel(String id, ArticleUpdateApiRequest request) {
        return ArticleUpdateRequest.builder().id(id).title(request.getTitle()).description(request.getDescription())
                .content(request.getContent()).categoryId(request.getCategoryId()).thumbnailImageUrl(request.getThumbnailImageUrl())
                .keywords(request.getKeywords()).hidden(request.getHidden()).estimatedReadingTime(request.getEstimatedReadingTime()).build();
    }

    private static ArticleCreationRequest toDomainModel(ArticleCreationApiRequest request) {
        return ArticleCreationRequest.builder().title(request.getTitle()).content(request.getContent())
                .description(request.getDescription()).categoryId(request.getCategoryId()).hidden(request.getHidden())
                .thumbnailImageUrl(request.getThumbnailImageUrl()).keywords(request.getKeywords())
                .estimatedReadingTime(request.getEstimatedReadingTime()).build();
    }
}
