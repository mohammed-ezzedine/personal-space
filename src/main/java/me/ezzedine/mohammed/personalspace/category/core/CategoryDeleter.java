package me.ezzedine.mohammed.personalspace.category.core;

import me.ezzedine.mohammed.personalspace.category.core.deletion.CategoryDeletionRejectedException;

public interface CategoryDeleter {
    void delete(String id) throws CategoryNotFoundException, CategoryDeletionRejectedException;
}
