package me.ezzedine.mohammed.personalspace.util.pagination;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationCriteria {
    private int startingPageIndex;
    private int maximumPageSize;
}
