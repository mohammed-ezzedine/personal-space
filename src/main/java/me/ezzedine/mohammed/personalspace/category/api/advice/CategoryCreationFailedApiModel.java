package me.ezzedine.mohammed.personalspace.category.api.advice;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryCreationFailedApiModel {
    private List<String> failureReasons;
}
