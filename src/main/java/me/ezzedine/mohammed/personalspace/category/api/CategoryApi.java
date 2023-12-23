package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.category.core.CategoryIdAlreadyExistsException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryValidationViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("categories")
public interface CategoryApi {

    @GetMapping
    ResponseEntity<List<CategorySummaryApiModel>> fetchCategoriesSummaries();

    @GetMapping("{id}")
    ResponseEntity<CategorySummaryApiModel> fetchCategoryDetails(@PathVariable String id) throws CategoryNotFoundException;

    @PostMapping
    ResponseEntity<CategoryCreationResultApiModel> create(@RequestBody CategoryCreationRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException;

    @PutMapping("orders")
    ResponseEntity<Void> updateCategoriesOrders(@RequestBody UpdateCategoriesOrdersApiRequest request);
}
