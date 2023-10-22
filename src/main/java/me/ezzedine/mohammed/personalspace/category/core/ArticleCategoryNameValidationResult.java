package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class ArticleCategoryNameValidationResult {

    private boolean valid;
    @Builder.Default
    private List<ArticleCategoryNameViolation> violations = new ArrayList<>();
}
