package com.tarasov.market.model.dto;

import com.tarasov.market.model.type.ActionType;
import com.tarasov.market.model.type.SortType;
import jakarta.validation.constraints.Positive;

public record CartUpdateRequest(
        @Positive Long id,
        String search,
        SortType sort,
        @Positive Integer pageNumber,
        @Positive Integer pageSize,
        ActionType action
) {

    public CartUpdateRequest() {
        this(null, "", SortType.NO, 1, 5, null);
    }
}
