package me.ezzedine.mohammed.personalspace.category.infra;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class CategoryEntity {
    private String id;
    private String name;
    private int order;
}
