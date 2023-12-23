package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("articles")
public interface ArticleApi {

    @GetMapping
    ResponseEntity<List<ArticleApiModel>> getArticles();

    @GetMapping("{id}")
    ResponseEntity<ArticleApiModel> getArticle(@PathVariable String id) throws ArticleNotFoundException;

    @PostMapping
    ResponseEntity<ArticleCreationApiResponse> create(@RequestBody ArticleCreationApiRequest request) throws CategoryNotFoundException;

    @PutMapping("{id}")
    ResponseEntity<Void> editArticle(@PathVariable String id, @RequestBody ArticleUpdateApiRequest request) throws CategoryNotFoundException, ArticleNotFoundException;
}
