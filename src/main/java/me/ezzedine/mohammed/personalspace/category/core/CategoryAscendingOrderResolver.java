package me.ezzedine.mohammed.personalspace.category.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryAscendingOrderResolver implements CategoryOrderResolver {

    private final CategoryStorage storage;

    @Override
    public int resolveOrderForNewCategory() {
        Optional<Category> category = storage.fetchCategoryWithHighestOrder();
        return category.map(value -> value.getOrder() + 1).orElse(1);
    }
}
