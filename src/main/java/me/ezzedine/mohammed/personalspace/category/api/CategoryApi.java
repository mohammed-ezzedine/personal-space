package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.category.core.CategoryIdAlreadyExistsException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import me.ezzedine.mohammed.personalspace.category.core.CategoryValidationViolationException;
import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("categories")
public interface CategoryApi {

    @GetMapping
    ResponseEntity<List<CategorySummaryApiModel>> fetchCategoriesSummaries();

    @GetMapping("{id}")
    ResponseEntity<CategorySummaryApiModel> fetchCategoryDetails(@PathVariable String id) throws CategoryNotFoundException;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    ResponseEntity<CategoryCreationResultApiModel> create(@RequestBody CategoryCreationRequest request) throws CategoryValidationViolationException, CategoryIdAlreadyExistsException;

    @PutMapping("orders")
    @PreAuthorize("hasAuthority('admin')")
    ResponseEntity<Void> updateCategoriesOrders(@RequestBody UpdateCategoriesOrdersApiRequest request);

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('admin')")
    ResponseEntity<Void> delete(@PathVariable String id) throws CategoryNotFoundException, CategoryDeletionRejectedException;
}
