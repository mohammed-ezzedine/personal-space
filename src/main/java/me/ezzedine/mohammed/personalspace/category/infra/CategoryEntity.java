package me.ezzedine.mohammed.personalspace.category.infra;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class CategoryEntity {
    private String id;
    private String name;
    private int order;
    @Accessors(fluent = true)
    private boolean canBeDeleted;
}
