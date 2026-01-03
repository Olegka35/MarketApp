package com.tarasov.market.service.impl;


import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.entity.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingCacheRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.ImageService;
import com.tarasov.market.service.OfferingService;
import com.tarasov.market.service.security.SecurityUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {

    private final OfferingRepository offeringRepository;
    private final ImageService imageService;
    private final OfferingCacheRepository offeringCacheRepository;
    private final CartRepository cartRepository;

    @Override
    public Mono<OfferingDto> getOffering(long id) {
        return SecurityUtils.getUserId()
                .flatMap(userId -> getOffering(userId, id))
                .switchIfEmpty(Mono.defer(() -> getOffering(null, id)));
    }

    private Mono<OfferingDto> getOffering(Long userId, long offeringId) {
        return loadOfferingFromCache(userId, offeringId)
                .switchIfEmpty(
                        Mono.defer(() -> loadOfferingFromDb(userId, offeringId))
                                .flatMap(this::saveOfferingInCache)
                );
    }

    @Override
    public Mono<OfferingPage> getOfferings(String search, SortType sortType, int pageNumber, int pageSize) {
        return SecurityUtils.getUserId()
                .flatMap(userId ->
                        getOfferings(userId, search, sortType, pageNumber, pageSize))
                .switchIfEmpty(Mono.defer(() -> getOfferings(null, search, sortType, pageNumber, pageSize)));
    }

    private Mono<OfferingPage> getOfferings(Long userId, String search, SortType sortType, int pageNumber, int pageSize) {
        return loadOfferingsFromCache(userId, search, sortType, pageNumber, pageSize)
                .switchIfEmpty(
                        Mono.defer(() -> loadOfferingsFromDb(userId, search, sortType, pageNumber, pageSize))
                                .flatMap(offeringPage ->
                                        saveOfferingPageInCache(search, sortType, pageNumber, pageSize, offeringPage))
                );
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Long> createOffering(String title, String description, BigDecimal price, FilePart image) {
        Offering offering = new Offering(title, description, image.filename(), price);
        return offeringRepository.save(offering)
                .flatMap(createdOffering -> imageService.uploadImage(image).thenReturn(createdOffering))
                .flatMap(createdOffering -> Mono.just(createdOffering.getId()))
                .flatMap(id -> offeringCacheRepository.clearCache().thenReturn(id));
    }

    private String convertSortTypeToField(SortType sortType) {
        return switch (sortType) {
            case ALPHA -> "offering_title";
            case PRICE -> "offering_price";
            default -> throw new IllegalArgumentException("Sort is not applicable");
        };
    }

    private Mono<OfferingDto> loadOfferingFromDb(Long userId, Long offeringId) {
        return loadOfferingDetails(userId, offeringId)
                .switchIfEmpty(Mono.error(new NoSuchElementException()))
                .map(OfferingDto::from);
    }

    private Mono<OfferingDto> loadOfferingFromCache(Long userId, Long offeringId) {
        return offeringCacheRepository.findByOfferingId(offeringId)
                .flatMap(offeringCache ->
                        countAmountInCart(userId, offeringId)
                                .map(amount -> collectOfferingDto(offeringCache, amount)));
    }

    private Mono<OfferingDto> saveOfferingInCache(OfferingDto offeringDto) {
        return offeringCacheRepository.saveOffering(OfferingCache.from(offeringDto))
                .thenReturn(offeringDto);
    }

    private Mono<OfferingPage> saveOfferingPageInCache(String search,
                                                       SortType sortType,
                                                       int pageNumber,
                                                       int pageSize,
                                                       OfferingPage offeringPage) {
        return offeringCacheRepository.saveOfferingPage(search,
                        sortType,
                        pageNumber,
                        pageSize,
                        OfferingPageCache.from(offeringPage))
                .thenReturn(offeringPage);
    }

    private OfferingDto collectOfferingDto(OfferingCache offeringCache, Integer amount) {
        return new OfferingDto(offeringCache.id(),
                offeringCache.title(),
                offeringCache.description(),
                offeringCache.imgPath(),
                offeringCache.price(),
                amount);
    }

    private Mono<OfferingPage> loadOfferingsFromCache(Long userId,
                                                      String search,
                                                      SortType sortType,
                                                      int pageNumber,
                                                      int pageSize) {
        return offeringCacheRepository.findOfferingPage(search, sortType, pageNumber, pageSize)
                .flatMap(cachedPage ->
                        countOfferingItemsInCart(userId, cachedPage)
                                .map(cartInfo ->
                                        collectOfferingPage(cachedPage, cartInfo)));
    }

    private Mono<OfferingPage> loadOfferingsFromDb(Long userId,
                                                   String search,
                                                   SortType sortType,
                                                   int pageNumber,
                                                   int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        if (!sortType.equals(SortType.NO)) {
            pageRequest.setSortField(convertSortTypeToField(sortType));
        }
        return Mono.zip(offeringRepository.findOfferings(userId, pageRequest, search)
                                .map(OfferingDto::from)
                                .collectList(),
                        getOfferingAmount(search))
                .map(tuple ->
                        new OfferingPage(tuple.getT1(), (tuple.getT2() + pageSize - 1) / pageSize));
    }

    private OfferingPage collectOfferingPage(OfferingPageCache offeringPageCache,
                                             Map<Long, Integer> cartInfoMap) {
        List<OfferingDto> offerings = offeringPageCache.offerings().stream()
                .map(offeringCache ->
                        collectOfferingDto(
                                offeringCache,
                                cartInfoMap.getOrDefault(offeringCache.id(), 0)
                        )
                )
                .toList();
        return new OfferingPage(offerings, offeringPageCache.totalPages());
    }

    private Mono<Integer> getOfferingAmount(String search) {
        return StringUtils.isEmpty(search)
                ? offeringRepository.count().map(Long::intValue)
                : offeringRepository.countByTitleContainingOrDescriptionContaining(search, search);
    }

    private Mono<Map<Long, Integer>> countOfferingItemsInCart(Long userId, OfferingPageCache cachedPage) {
        Mono<Map<Long, Integer>> offeringsInCartMap = Mono.just(Map.of());
        if (userId != null) {
            List<Long> offeringIds = cachedPage.offerings()
                    .stream()
                    .map(OfferingCache::id)
                    .toList();
            offeringsInCartMap = cartRepository.findByOfferingIdInAndUserId(offeringIds, userId)
                    .collectMap(CartItem::getOfferingId, CartItem::getAmount);
        }
        return offeringsInCartMap;
    }

    private Mono<Integer> countAmountInCart(Long userId, Long offeringId) {
        Mono<Integer> amountInCart = Mono.just(0);
        if (userId != null) {
            amountInCart = cartRepository.findByOfferingIdAndUserId(offeringId, userId)
                    .map(CartItem::getAmount)
                    .switchIfEmpty(Mono.just(0));
        }
        return amountInCart;
    }

    private Mono<OfferingWithCartItem> loadOfferingDetails(Long userId, Long offeringId) {
        if (userId == null) {
            return offeringRepository.findByIdWithEmptyCart(offeringId);
        }
        return offeringRepository.findByIdWithCart(offeringId, userId);
    }
}
