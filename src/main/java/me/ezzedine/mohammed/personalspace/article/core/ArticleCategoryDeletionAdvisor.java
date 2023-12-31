package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermission;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionPermissionGranter;
import me.ezzedine.mohammed.personalspace.util.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleCategoryDeletionAdvisor implements CategoryDeletionPermissionGranter {

    private final ArticleStorage articleStorage;

    @Override
    public CategoryDeletionPermission canDeleteCategory(String categoryId) {
        Page<Article> articles = articleStorage.fetchAll(ArticlesFetchCriteria.builder().categoryId(categoryId).build());
        if (articles.getItems().isEmpty()) {
            return CategoryDeletionPermission.allowed();
        }

        return CategoryDeletionPermission.notAllowed(getReasonMessage(articles.getItems()));
    }

    private String getReasonMessage(List<Article> articles) {
        String commaSeparatedArticleIds = articles.stream().map(Article::getId).reduce((id1, id2) -> id1 + ", " + id2).orElse("");
        return "Category cannot be deleted since it is linked to the following articles: [" + commaSeparatedArticleIds + "]";
    }
}
