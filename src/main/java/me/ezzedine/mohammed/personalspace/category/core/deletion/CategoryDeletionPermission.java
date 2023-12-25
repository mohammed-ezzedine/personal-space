package me.ezzedine.mohammed.personalspace.category.core.deletion;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class CategoryDeletionPermission {
    private boolean allowed;
    private String message;

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public static CategoryDeletionPermission allowed() {
        return CategoryDeletionPermission.builder().allowed(true).build();
    }

    public static CategoryDeletionPermission notAllowed(String reason) {
        return CategoryDeletionPermission.builder().allowed(false).message(reason).build();
    }
}
