package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Builder
public class Category {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private int order;
    @Accessors(fluent = true)
    private boolean canBeDeleted;
}
