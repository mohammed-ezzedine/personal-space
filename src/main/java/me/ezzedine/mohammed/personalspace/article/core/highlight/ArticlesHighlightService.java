package me.ezzedine.mohammed.personalspace.article.core.highlight;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.Article;
import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import me.ezzedine.mohammed.personalspace.article.core.ArticleStorage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticlesHighlightService implements ArticlesHighlightUpdater, ArticlesHighlightFetcher {

    private final ArticlesHighlightStorage highlightStorage;
    private final ArticleStorage articleStorage;

    @Override
    public void updateArticlesHighlights(List<HighlightedArticle> articles) {
        List<HighlightedArticle> existingArticles = new ArrayList<>();
        articles.forEach(article -> {
            boolean articleHighlightIsAlreadyAdded = existingArticles.stream().anyMatch(a -> a.getArticleId().equals(article.getArticleId()));
            boolean articleDoesNotExists = articleStorage.fetch(article.getArticleId()).isEmpty();
            if (articleHighlightIsAlreadyAdded || articleDoesNotExists) {
                return;
            }

            existingArticles.add(article);
        });

        highlightStorage.save(existingArticles);
    }

    @Override
    public void addArticleToHighlights(String articleId) throws ArticleAlreadyHighlightedException, ArticleNotFoundException {
        validateArticleExists(articleId);

        List<HighlightedArticle> highlightedArticles = new ArrayList<>(highlightStorage.getHighlightedArticles());
        if (articleIsHighlighted(articleId, highlightedArticles)) {
            throw new ArticleAlreadyHighlightedException(articleId);
        }

        highlightedArticles.add(HighlightedArticle.builder().articleId(articleId).highlightRank(highlightedArticles.size() + 1).build());
        highlightStorage.save(highlightedArticles);
    }

    @Override
    public void removeArticleFromHighlights(String articleId) throws ArticleWasNotHighlightedException, ArticleNotFoundException {
        validateArticleExists(articleId);

        List<HighlightedArticle> highlightedArticles = new ArrayList<>(highlightStorage.getHighlightedArticles());
        if(!articleIsHighlighted(articleId, highlightedArticles)) {
            throw new ArticleWasNotHighlightedException(articleId);
        }

        removeArticleFromHighlights(articleId, highlightedArticles);
        highlightStorage.save(highlightedArticles);
    }

    @Override
    public List<Article> getHighlightedArticles() {
        List<HighlightedArticle> highlightedArticlesIds = highlightStorage.getHighlightedArticles();
        return highlightedArticlesIds.stream()
                .map(a -> articleStorage.fetch(a.getArticleId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<HighlightedArticle> getHighlightedArticlesSummary() {
        return highlightStorage.getHighlightedArticles();
    }

    private void validateArticleExists(String articleId) throws ArticleNotFoundException {
        if (articleStorage.fetch(articleId).isEmpty()) {
            throw new ArticleNotFoundException(articleId);
        }
    }

    private static void removeArticleFromHighlights(String articleId, List<HighlightedArticle> highlightedArticles) {
        highlightedArticles.removeIf(a -> a.getArticleId().equals(articleId));
        for (int i = 0; i < highlightedArticles.size(); i++) {
            highlightedArticles.get(i).setHighlightRank(i + 1);
        }
    }

    private static boolean articleIsHighlighted(String articleId, List<HighlightedArticle> highlightedArticles) {
        return highlightedArticles.stream().anyMatch(a -> a.getArticleId().equals(articleId));
    }
}
