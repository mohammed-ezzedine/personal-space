package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.category.core.CategoryIdAlreadyExistsException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryValidationViolationException;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;
import me.ezzedine.mohammed.personalspace.config.security.AdminAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/categories")
public interface CategoryApi {

    @GetMapping
    ResponseEntity<List<CategorySummaryApiModel>> fetchCategoriesSummaries();

    @GetMapping("{id}")
    ResponseEntity<CategorySummaryApiModel> fetchCategoryDetails(@PathVariable String id) throws CategoryNotFoundException;

    @PostMapping
    @AdminAccess
    ResponseEntity<CategoryCreationResultApiModel> create(@RequestBody CategoryCreationRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException;

    @PutMapping("orders")
    @AdminAccess
    ResponseEntity<Void> updateCategoriesOrders(@RequestBody UpdateCategoriesOrdersApiRequest request);

    @DeleteMapping("{id}")
    @AdminAccess
    ResponseEntity<Void> delete(@PathVariable String id) throws CategoryNotFoundException, CategoryDeletionRejectedException;
}
