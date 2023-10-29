package me.ezzedine.mohammed.personalspace.category.core;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryNameAlphaValidator implements CategoryNameValidator {

    @Override
    public CategoryNameValidationResult validate(String name) {
        boolean valid = true;
        List<CategoryNameViolation> violations = new ArrayList<>();

        name = name.trim();

        if (nameIsValid(name)) {
            return CategoryNameValidationResult.builder().valid(valid).violations(violations).build();
        }

        if (name.trim().isEmpty()) {
            valid = false;
            violations.add(CategoryNameViolation.NOT_EMPTY);
        }

        if (nameHasDigits(name)) {
            valid = false;
            violations.add(CategoryNameViolation.NO_DIGITS);
        }

        if (nameHasSpecialCharacter(name)) {
            valid = false;
            violations.add(CategoryNameViolation.NO_SPECIAL_CHARACTERS);
        }

        return CategoryNameValidationResult.builder().valid(valid).violations(violations).build();
    }

    private boolean nameHasSpecialCharacter(String name) {
        return !name.isEmpty() && !name.matches("[a-zA-Z 0-9]+");
    }

    private static boolean nameIsValid(String name) {
        return name.matches("[a-zA-Z][a-zA-Z ]*");
    }

    private static boolean nameHasDigits(String name) {
        return !name.isEmpty() && name.matches(".*\\d+.*");
    }
}
