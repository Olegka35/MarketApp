package com.tarasov.market.service.impl;


import com.tarasov.market.model.entity.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.ImageService;
import com.tarasov.market.service.OfferingService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {

    private final OfferingRepository offeringRepository;
    private final ImageService imageService;

    @Override
    public Mono<OfferingDto> getOffering(long id) {
        return offeringRepository.findByIdWithCart(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(OfferingDto::from);
    }

    @Override
    public Mono<OfferingPage> getOfferings(String search, SortType sortType, int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        if (!sortType.equals(SortType.NO)) {
            pageRequest.setSortField(convertSortTypeToField(sortType));
        }
        Mono<Integer> offeringAmount = StringUtils.isEmpty(search)
                ? offeringRepository.count().map(Long::intValue)
                : offeringRepository.countByTitleContainingOrDescriptionContaining(search, search);
        return Mono.zip(offeringRepository.findOfferings(pageRequest, search)
                                .map(OfferingDto::from).collectList(),
                        offeringAmount)
                .map(tuple ->
                        new OfferingPage(tuple.getT1(), (tuple.getT2() + pageSize - 1) / pageSize));
    }

    @Override
    @Transactional
    public Mono<Long> createOffering(String title, String description, BigDecimal price, FilePart image) {
        Offering offering = new Offering(title, description, image.filename(), price);
        return offeringRepository.save(offering)
                .flatMap(createdOffering -> imageService.uploadImage(image).thenReturn(createdOffering))
                .flatMap(createdOffering -> Mono.just(createdOffering.getId()));
    }

    private String convertSortTypeToField(SortType sortType) {
        return switch (sortType) {
            case ALPHA -> "offering_title";
            case PRICE -> "offering_price";
            default -> throw new IllegalArgumentException("Sort is not applicable");
        };
    }
}
