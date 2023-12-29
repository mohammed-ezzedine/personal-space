package me.ezzedine.mohammed.personalspace.article.core;

import lombok.Builder;
import lombok.Data;
import me.ezzedine.mohammed.personalspace.util.pagination.PaginationCriteria;
import me.ezzedine.mohammed.personalspace.util.sort.SortingCriteria;

import java.util.Optional;

@Data
@Builder
public class ArticlesFetchCriteria {
    private PaginationCriteria paginationCriteria;
    private SortingCriteria sortingCriteria;
    private Boolean highlighted;
    private String categoryId;
    private Boolean hidden;

    public Optional<PaginationCriteria> getPaginationCriteria() {
        return Optional.ofNullable(paginationCriteria);
    }

    public Optional<Boolean> getHighlighted() {
        return Optional.ofNullable(highlighted);
    }

    public Optional<String> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public Optional<SortingCriteria> getSortingCriteria() {
        return Optional.ofNullable(sortingCriteria);
    }

    public Optional<Boolean> getHidden() {
        return Optional.ofNullable(hidden);
    }
}
