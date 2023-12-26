package me.ezzedine.mohammed.personalspace.article.api.highlight;

import me.ezzedine.mohammed.personalspace.article.api.ArticleSummaryApiModel;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticleAlreadyHighlightedException;
import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticleWasNotHighlightedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("articles/highlight")
public interface HighlightedArticlesApi {

    @PostMapping("{id}")
    ResponseEntity<Void> addArticleToHighlights(@PathVariable String id) throws ArticleNotFoundException, ArticleAlreadyHighlightedException;

    @DeleteMapping("{id}")
    ResponseEntity<Void> removeArticleFromHighlights(@PathVariable String id) throws ArticleNotFoundException, ArticleWasNotHighlightedException;

    @PutMapping
    ResponseEntity<Void> updateHighlightedArticles(@RequestBody HighlightedArticlesUpdateRequest request);

    @GetMapping
    ResponseEntity<List<ArticleSummaryApiModel>> getHighlightedArticles();

    @GetMapping("summary")
    ResponseEntity<List<ArticleHighlightApiModel>> getHighlightedArticlesSummary();
}
