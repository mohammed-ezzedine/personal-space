package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArticleCategoryNameViolation {
    NO_DIGITS("The category name cannot contain digits"),
    NO_SPECIAL_CHARACTERS("The category name cannot contain a special character"),
    NOT_EMPTY("The category name cannot be empty");

    private final String message;
}
