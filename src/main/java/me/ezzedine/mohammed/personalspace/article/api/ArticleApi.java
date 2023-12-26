package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("articles")
public interface ArticleApi {

    @GetMapping
    ResponseEntity<Page<ArticleSummaryApiModel>> getArticles(ArticlesFetchApiCriteria fetchCriteria);

    @GetMapping("{id}")
    ResponseEntity<ArticleApiModel> getArticle(@PathVariable String id) throws ArticleNotFoundException;

    @PostMapping
    ResponseEntity<ArticleCreationApiResponse> create(@RequestBody ArticleCreationApiRequest request) throws CategoryNotFoundException;

    @PutMapping("{id}")
    ResponseEntity<Void> editArticle(@PathVariable String id, @RequestBody ArticleUpdateApiRequest request) throws CategoryNotFoundException, ArticleNotFoundException;
}
