package com.tarasov.market.model.dto;

import com.tarasov.market.model.type.ActionType;
import com.tarasov.market.model.type.SortType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartUpdateRequest(
        @Positive Long id,
        String search,
        SortType sort,
        @Positive Integer pageNumber,
        @Positive Integer pageSize,
        @NotNull ActionType action
) {

    public CartUpdateRequest {
        if (search == null) {
            search = "";
        }
        if (sort == null) {
            sort = SortType.NO;
        }
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }
    }
}
