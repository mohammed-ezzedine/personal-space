package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategorySpaceToHyphenIdGeneratorTest {

    private CategorySpaceToHyphenIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new CategorySpaceToHyphenIdGenerator();
    }

    @Test
    @DisplayName("it should replace return the category name as is if it is a single lowercase word")
    void it_should_replace_return_the_category_name_as_is_if_it_is_a_single_lowercase_word() {
        assertEquals("name", idGenerator.generate("name"));
    }

    @Test
    @DisplayName("it should trim spaces at the beginning of the name")
    void it_should_trim_spaces_at_the_beginning_of_the_name() {
        assertEquals("name", idGenerator.generate(" name"));
    }

    @Test
    @DisplayName("it should trim spaces at the end of the name")
    void it_should_trim_spaces_at_the_end_of_the_name() {
        assertEquals("name", idGenerator.generate("name "));
    }

    @Test
    @DisplayName("it should convert upper case letters in the category name to lower case")
    void it_should_convert_upper_case_letters_in_the_category_name_to_lower_case() {
        assertEquals("name", idGenerator.generate("nAmE"));
    }

    @Test
    @DisplayName("it should replace spaces in the name with hyphens")
    void it_should_replace_spaces_in_the_name_with_hyphens() {
        assertEquals("the-name", idGenerator.generate("the name"));
    }
}