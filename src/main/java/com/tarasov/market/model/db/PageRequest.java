package com.tarasov.market.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequest {
    private int pageNumber;
    private int pageSize;
    private String sortField;

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
