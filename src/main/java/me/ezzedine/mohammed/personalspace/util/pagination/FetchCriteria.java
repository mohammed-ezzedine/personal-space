package me.ezzedine.mohammed.personalspace.util.pagination;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchCriteria {
    private int startingPageIndex;
    private int maximumPageSize;
}
