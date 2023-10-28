package me.ezzedine.mohammed.personalspace.category.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySummaryApiModel {
    private String id;
    private String name;
    private boolean canBeDeleted;
}
