package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CategoryNameAlphaValidatorTest {

    private CategoryNameAlphaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CategoryNameAlphaValidator();
    }

    @Test
    @DisplayName("the name should at least contain one character")
    void the_name_should_at_least_contain_one_character() {
        CategoryNameValidationResult result = validator.validate("");
        assertFalse(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(1, result.getViolations().size());
        assertEquals(CategoryNameViolation.NOT_EMPTY, result.getViolations().get(0));
    }

    @Test
    @DisplayName("the name should at least contain one character other than a space")
    void the_name_should_at_least_contain_one_character_other_than_a_space() {
        CategoryNameValidationResult result = validator.validate(" ");
        assertFalse(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(1, result.getViolations().size());
        assertEquals(CategoryNameViolation.NOT_EMPTY, result.getViolations().get(0));
    }

    @ParameterizedTest
    @MethodSource("getNamesWithDigits")
    @DisplayName("the name should not be valid if it contains numbers")
    void the_name_should_not_be_valid_if_it_contains_numbers(String nameWithDigits) {
        CategoryNameValidationResult result = validator.validate(nameWithDigits);
        assertFalse(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(1, result.getViolations().size(), String.format("Validation result is %s", result));
        assertEquals(CategoryNameViolation.NO_DIGITS, result.getViolations().get(0));
    }

    @ParameterizedTest
    @MethodSource("getNamesWithSpecialCharacters")
    @DisplayName("the name should not be valid if it contains special characters")
    void the_name_should_not_be_valid_if_it_contains_special_characters(String invalidName) {
        CategoryNameValidationResult result = validator.validate(invalidName);
        assertFalse(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(1, result.getViolations().size());
        assertEquals(CategoryNameViolation.NO_SPECIAL_CHARACTERS, result.getViolations().get(0));
    }

    @ParameterizedTest
    @MethodSource("getNamesWithPunctuations")
    @DisplayName("the name should not be valid if it contains punctuations")
    void the_name_should_not_be_valid_if_it_contains_special_punctuations(String invalidName) {
        CategoryNameValidationResult result = validator.validate(invalidName);
        assertFalse(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(1, result.getViolations().size());
        assertEquals(CategoryNameViolation.NO_SPECIAL_CHARACTERS, result.getViolations().get(0));
    }

    @Test
    @DisplayName("the name should be valid if it only contains alpha characters with spaces")
    void the_name_should_be_valid_if_it_only_contains_alpha_characters_with_spaces() {
        CategoryNameValidationResult result = validator.validate("Hello World");
        assertTrue(result.isValid(), String.format("Validation result is %s", result));
        assertEquals(0, result.getViolations().size());
    }

    private static Stream<Character> getSpecialCharacters() {
        return Stream.of('`', '@', '#', '$', '%', '^', '&', '*', '_', '-', '=', '+', '\\', '<', '>','/', '~');
    }


    private static Stream<Character> getPunctuations() {
        return Stream.of( '!', '(', ')', '[', ']', ';', ':', '\'', '"', ',', '?', '.');
    }

    public static Stream<Arguments> getNamesWithSpecialCharacters() {
        return getSpecialCharacters().map(String::valueOf).map(Arguments::of);
    }

    public static Stream<Arguments> getNamesWithPunctuations() {
        return getPunctuations().map(String::valueOf).map(Arguments::of);
    }

    public static Stream<Arguments> getNamesWithDigits() {
        return Stream.of(
                Arguments.of("1"),
                Arguments.of("1a"),
                Arguments.of("b3"),
                Arguments.of("b35"),
                Arguments.of("09"),
                Arguments.of("0"),
                Arguments.of("abc 4"),
                Arguments.of("ab3c c"),
                Arguments.of("ab355c c"),
                Arguments.of("ab355c 6")
        );
    }
}