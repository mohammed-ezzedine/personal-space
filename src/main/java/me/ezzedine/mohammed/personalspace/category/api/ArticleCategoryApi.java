package me.ezzedine.mohammed.personalspace.category.api;

import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryIdAlreadyExistsException;
import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryValidationViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/categories")
public interface ArticleCategoryApi {

    @GetMapping
    ResponseEntity<List<ArticleCategorySummaryApiModel>> fetchCategoriesSummaries();

    @PostMapping
    ResponseEntity<CategoryCreationResultApiModel> create(@RequestBody CategoryCreationRequest request) throws ArticleCategoryValidationViolationException, ArticleCategoryIdAlreadyExistsException;
}
