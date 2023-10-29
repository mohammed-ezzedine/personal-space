package me.ezzedine.mohammed.personalspace.category.core;

import java.util.List;

public interface CategoryFetcher {
    List<Category> fetchAll();
    boolean exists(String id);
}
