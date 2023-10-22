package me.ezzedine.mohammed.personalspace.category.core;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleCategoryNameAlphaValidator implements ArticleCategoryNameValidator {

    @Override
    public ArticleCategoryNameValidationResult validate(String name) {
        boolean valid = true;
        List<ArticleCategoryNameViolation> violations = new ArrayList<>();

        name = name.trim();

        if (nameIsValid(name)) {
            return ArticleCategoryNameValidationResult.builder().valid(valid).violations(violations).build();
        }

        if (name.trim().isEmpty()) {
            valid = false;
            violations.add(ArticleCategoryNameViolation.NOT_EMPTY);
        }

        if (nameHasDigits(name)) {
            valid = false;
            violations.add(ArticleCategoryNameViolation.NO_DIGITS);
        }

        if (nameHasSpecialCharacter(name)) {
            valid = false;
            violations.add(ArticleCategoryNameViolation.NO_SPECIAL_CHARACTERS);
        }

        return ArticleCategoryNameValidationResult.builder().valid(valid).violations(violations).build();
    }

    private boolean nameHasSpecialCharacter(String name) {
        return !name.isEmpty() && !name.matches(".*[a-zA-Z 0-9]+.*");
    }

    private static boolean nameIsValid(String name) {
        return name.matches("[a-zA-Z][a-zA-Z ]*");
    }

    private static boolean nameHasDigits(String name) {
        return !name.isEmpty() && name.matches(".*\\d+.*");
    }
}
