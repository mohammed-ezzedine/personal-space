package me.ezzedine.mohammed.personalspace.category.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateCategoriesOrdersRequest {
    private List<CategoryOrder> categoryOrders;

    @Data
    @Builder
    public static class CategoryOrder {
        private String categoryId;
        private int order;
    }
}
