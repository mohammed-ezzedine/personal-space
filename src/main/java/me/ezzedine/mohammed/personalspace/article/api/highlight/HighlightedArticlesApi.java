package me.ezzedine.mohammed.personalspace.article.api.highlight;

import me.ezzedine.mohammed.personalspace.article.api.ArticleSummaryApiModel;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticleAlreadyHighlightedException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticleWasNotHighlightedException;
import me.ezzedine.mohammed.personalspace.config.security.AdminAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/articles/highlight")
public interface HighlightedArticlesApi {

    @PostMapping("{id}")
    @AdminAccess
    ResponseEntity<Void> addArticleToHighlights(@PathVariable String id) throws ArticleNotFoundException, ArticleAlreadyHighlightedException;

    @DeleteMapping("{id}")
    @AdminAccess
    ResponseEntity<Void> removeArticleFromHighlights(@PathVariable String id) throws ArticleNotFoundException, ArticleWasNotHighlightedException;

    @PutMapping
    @AdminAccess
    ResponseEntity<Void> updateHighlightedArticles(@RequestBody HighlightedArticlesUpdateRequest request);

    @GetMapping
    ResponseEntity<List<ArticleSummaryApiModel>> getHighlightedArticles();

    @GetMapping("summary")
    @AdminAccess
    ResponseEntity<List<ArticleHighlightApiModel>> getHighlightedArticlesSummary();
}
