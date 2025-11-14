package com.tarasov.market.service.impl;


import com.tarasov.market.model.ImageUploadException;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.type.SortType;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.ImageService;
import com.tarasov.market.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {

    private final OfferingRepository offeringRepository;
    private final ImageService imageService;

    @Override
    public OfferingDto getOffering(long id) {
        return OfferingDto.from(offeringRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public OfferingPage getOfferings(String search, SortType sortType, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        if (!sortType.equals(SortType.NO)) {
            pageRequest = pageRequest.withSort(Sort.by(Sort.Direction.ASC, convertSortType(sortType)));
        }
        Page<Offering> offeringPage;
        if (search.isEmpty()) {
            offeringPage = offeringRepository.findAll(pageRequest);
        } else {
            offeringPage = offeringRepository.findByTitleContainsOrDescriptionContains(search, search, pageRequest);
        }
        return new OfferingPage(offeringPage.getContent().stream().map(OfferingDto::from).toList(),
                offeringPage.getTotalPages());
    }

    @Override
    @Transactional
    public Long createOffering(String title, String description, BigDecimal price, MultipartFile image) {
        Offering offering = new Offering(title, description, image.getOriginalFilename(), price);
        offering = offeringRepository.save(offering);

        imageService.uploadImage(image);
        return offering.getId();
    }

    private String convertSortType(SortType sortType) {
        return switch (sortType) {
            case ALPHA -> "title";
            case PRICE -> "price";
            default -> throw new IllegalArgumentException("Sort is not applicable");
        };
    }
}
