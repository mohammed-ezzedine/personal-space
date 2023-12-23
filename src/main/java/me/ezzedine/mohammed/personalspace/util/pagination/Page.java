package me.ezzedine.mohammed.personalspace.util.pagination;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Page<T> {
    private long totalSize;
    private List<T> items;
}
