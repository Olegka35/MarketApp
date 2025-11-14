package com.tarasov.market.service;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.type.SortType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public interface OfferingService {
    OfferingDto getOffering(long id);
    OfferingPage getOfferings(String search, SortType sortType, int pageNumber, int pageSize);
    Long createOffering(String title, String description, BigDecimal price, MultipartFile image);
}
