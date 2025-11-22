package com.tarasov.market.service;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.type.SortType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


public interface OfferingService {
    Mono<OfferingDto> getOffering(long id);
    Mono<OfferingPage> getOfferings(String search, SortType sortType, int pageNumber, int pageSize);
    Mono<Long> createOffering(String title, String description, BigDecimal price, MultipartFile image);
}
