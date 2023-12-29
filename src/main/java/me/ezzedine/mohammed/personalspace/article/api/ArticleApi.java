package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.config.security.AdminAccess;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("articles")
public interface ArticleApi {

    @GetMapping
    ResponseEntity<Page<ArticleSummaryApiModel>> getArticles(ArticlesFetchApiCriteria fetchCriteria, Principal principal);

    @GetMapping("{id}")
    ResponseEntity<ArticleApiModel> getArticle(@PathVariable String id) throws ArticleNotFoundException;

    @PostMapping
    @AdminAccess
    ResponseEntity<ArticleCreationApiResponse> create(@RequestBody ArticleCreationApiRequest request) throws CategoryNotFoundException;

    @PutMapping("{id}")
    @AdminAccess
    ResponseEntity<Void> editArticle(@PathVariable String id, @RequestBody ArticleUpdateApiRequest request) throws CategoryNotFoundException, ArticleNotFoundException;
}
