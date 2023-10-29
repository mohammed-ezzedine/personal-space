package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Category {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private int order;
}
