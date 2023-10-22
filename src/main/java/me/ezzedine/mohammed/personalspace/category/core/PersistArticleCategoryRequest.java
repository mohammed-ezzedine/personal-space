package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class PersistArticleCategoryRequest {
    @NonNull
    private String name;
}
