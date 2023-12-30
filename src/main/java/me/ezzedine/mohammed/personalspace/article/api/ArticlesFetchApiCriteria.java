package me.ezzedine.mohammed.personalspace.article.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesFetchApiCriteria {
    private Integer page;
    private Integer size;
    private Boolean highlighted;
    private String sortBy;
    private Boolean ascOrder;
    private String categoryId;

    public Optional<Integer> getPage() {
        return Optional.ofNullable(page);
    }

    public Optional<Integer> getSize() {
        return Optional.ofNullable(size);
    }

    public Optional<String> getSortBy() {
        return Optional.ofNullable(sortBy);
    }

    public Optional<Boolean> getAscOrder() {
        return Optional.ofNullable(ascOrder);
    }
}
