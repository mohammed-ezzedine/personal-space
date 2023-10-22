package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
public class ArticleCategory {
    private String id;
    private String name;
    @Accessors(fluent = true)
    private boolean canBeDeleted;
}
