package me.ezzedine.mohammed.personalspace.category.core.deletion;

public interface CategoryDeletionPermissionGranter {
    CategoryDeletionPermission canDeleteCategory(String categoryId);
}
