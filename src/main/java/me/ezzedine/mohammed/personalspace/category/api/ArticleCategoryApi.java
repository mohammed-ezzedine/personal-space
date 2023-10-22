package me.ezzedine.mohammed.personalspace.category.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/categories")
public interface ArticleCategoryApi {

    @GetMapping
    ResponseEntity<List<ArticleCategorySummaryApiModel>> fetchCategoriesSummaries();
}
