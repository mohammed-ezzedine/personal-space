package me.ezzedine.mohammed.personalspace.category.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryCreationResultApiModel {
    private String id;
    private int order;
}
