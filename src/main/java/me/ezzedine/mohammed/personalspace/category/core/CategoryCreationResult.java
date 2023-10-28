package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryCreationResult {
    private String id;
    private int order;
}
