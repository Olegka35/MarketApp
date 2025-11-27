package com.tarasov.market.model.dto;

import com.tarasov.market.model.type.ActionType;
import com.tarasov.market.model.type.SortType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Optional;

public record CartUpdateRequest(
        @Positive Long id,
        String search,
        SortType sort,
        @Positive Integer pageNumber,
        @Positive Integer pageSize,
        @NotNull ActionType action
) {

    public CartUpdateRequest(Long id,
                             String search,
                             SortType sort,
                             Integer pageNumber,
                             Integer pageSize,
                             ActionType action) {
        this.id = id;
        this.search = Optional.ofNullable(search).orElse("");
        this.sort = Optional.ofNullable(sort).orElse(SortType.NO);
        this.pageNumber = Optional.ofNullable(pageNumber).orElse(1);
        this.pageSize = Optional.ofNullable(pageSize).orElse(5);
        this.action = action;
    }
}
