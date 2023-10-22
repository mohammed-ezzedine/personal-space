package me.ezzedine.mohammed.personalspace.category.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
public class ArticleCategorySummaryApiModel {
    private String id;
    private String name;
    private boolean canBeDeleted;
}
