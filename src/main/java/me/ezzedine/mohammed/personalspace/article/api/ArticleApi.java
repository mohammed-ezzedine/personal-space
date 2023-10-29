package me.ezzedine.mohammed.personalspace.article.api;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/articles")
public interface ArticleApi {

    @PostMapping
    ResponseEntity<ArticleCreationApiResponse> create(@RequestBody ArticleCreationApiRequest request) throws CategoryNotFoundException;

    @GetMapping("{id}")
    ResponseEntity<ArticleApiModel> getArticle(@PathVariable String id) throws ArticleNotFoundException;
}
