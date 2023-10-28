package me.ezzedine.mohammed.personalspace.category.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoriesOrdersApiRequest {

    private List<CategoryOrderApiRequest> categoriesOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryOrderApiRequest {
        private String categoryId;
        private int categoryOrder;
    }
}
