package com.tarasov.market.service;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.SortType;


public interface OfferingService {
    OfferingDto getOffering(long id);
    OfferingPage getOfferings(String search, SortType sortType, int pageNumber, int pageSize);
}
