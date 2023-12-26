package me.ezzedine.mohammed.personalspace.util.sort;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortingCriteria {
    private String field;
    private boolean ascendingOrder;
}
