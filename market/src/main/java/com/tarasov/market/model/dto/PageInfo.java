package com.tarasov.market.model.dto;

public record PageInfo(int pageSize,
                       int pageNumber,
                       boolean hasPrevious,
                       boolean hasNext) {
}
