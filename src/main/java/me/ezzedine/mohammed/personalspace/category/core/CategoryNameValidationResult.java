package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CategoryNameValidationResult {

    private boolean valid;
    @Builder.Default
    private List<CategoryNameViolation> violations = new ArrayList<>();
}
