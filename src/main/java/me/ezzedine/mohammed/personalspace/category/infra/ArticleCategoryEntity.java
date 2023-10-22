package me.ezzedine.mohammed.personalspace.category.infra;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class ArticleCategoryEntity {
    private String id;
    private String name;
    @Accessors(fluent = true)
    private boolean canBeDeleted;
}
