package me.ezzedine.mohammed.personalspace.category.core;

import java.util.List;

public interface CategoryFetcher {
    List<Category> fetchAll();
    Category fetch(String id) throws CategoryNotFoundException;
}
