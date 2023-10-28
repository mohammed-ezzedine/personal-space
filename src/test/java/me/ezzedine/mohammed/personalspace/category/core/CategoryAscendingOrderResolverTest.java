package me.ezzedine.mohammed.personalspace.category.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryAscendingOrderResolverTest {

    private CategoryStorage storage;
    private CategoryAscendingOrderResolver orderResolver;

    @BeforeEach
    void setUp() {
        storage = mock(CategoryStorage.class);
        orderResolver = new CategoryAscendingOrderResolver(storage);
    }

    @Nested
    @DisplayName("When resolving an order for a new category")
    class ResolvingOrderForNewCategoryTest {

        @Test
        @DisplayName("should return one when there is no category in the storage")
        void should_return_one_when_there_is_no_category_in_the_storage() {
            when(storage.fetchCategoryWithHighestOrder()).thenReturn(Optional.empty());
            int order = orderResolver.resolveOrderForNewCategory();
            assertEquals(1, order);
        }

        @Test
        @DisplayName("should return an order higher by the highest existing order by one")
        void should_return_an_order_higher_by_the_highest_existing_order_by_one() {
            int highestExistingOrder = new Random().nextInt();
            when(storage.fetchCategoryWithHighestOrder()).thenReturn(Optional.of(getCategory(highestExistingOrder)));
            int order = orderResolver.resolveOrderForNewCategory();
            assertEquals(highestExistingOrder + 1, order);
        }
    }

    private static Category getCategory(int order) {
        return Category.builder().id(UUID.randomUUID().toString()).name(UUID.randomUUID().toString()).order(order).build();
    }
}