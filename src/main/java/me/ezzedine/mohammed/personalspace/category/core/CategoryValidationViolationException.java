package me.ezzedine.mohammed.personalspace.category.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryValidationViolationException extends Exception {

    private final List<String> reasons;
}
