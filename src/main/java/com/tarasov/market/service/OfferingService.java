package com.tarasov.market.service;


import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.SortType;


public interface OfferingService {
    OfferingPage getOfferings(String search, SortType sortType, int pageNumber, int pageSize);
}
