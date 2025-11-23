package com.tarasov.market.service;


import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.model.entity.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.impl.OfferingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OfferingServiceTest {

    @InjectMocks
    private OfferingServiceImpl offeringService;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private ImageService imageService;

    @Test
    public void getOfferingByIdTest() {
        long ID = 5L;
        OfferingWithCartItem mockOffering = generateTestOffering(ID);
        when(offeringRepository.findByIdWithCart(ID)).thenReturn(Mono.just(mockOffering));

        OfferingDto offeringDto = offeringService.getOffering(ID).block();

        assertNotNull(offeringDto);
        assertEquals(ID, offeringDto.id());
        assertEquals("Test", offeringDto.title());
        assertEquals("Test description", offeringDto.description());
        assertEquals("img.png", offeringDto.imgPath());
        assertEquals(BigDecimal.valueOf(100L), offeringDto.price());
        assertEquals(3, offeringDto.count());
    }

    @Test
    public void getOfferingByIdTest_notFound() {
        long ID = 5L;
        when(offeringRepository.findByIdWithCart(ID)).thenReturn(Mono.empty());

        assertThrows(ResponseStatusException.class, () -> offeringService.getOffering(ID).block());
    }

    @Test
    public void searchOfferingsTest_checkResponseProcessing() {
        PageRequest pageRequest = new PageRequest(1, 5);
        when(offeringRepository.findOfferings(pageRequest, ""))
                .thenReturn(Flux.just(generateTestOffering(1L), generateTestOffering(4L)));
        when(offeringRepository.count()).thenReturn(Mono.just(2L));

        OfferingPage page = offeringService.getOfferings("", SortType.NO, 1, 5).block();

        assertNotNull(page);
        assertEquals(2, page.getOfferings().size());
        OfferingDto lastOffering = page.getOfferings().getLast();
        assertEquals("Test", lastOffering.title());
        assertEquals("Test description", lastOffering.description());
        assertEquals("img.png", lastOffering.imgPath());
        assertEquals(BigDecimal.valueOf(100L), lastOffering.price());
        assertEquals(3, lastOffering.count());
        assertEquals(1, page.getTotalPages());

        verify(offeringRepository).findOfferings(pageRequest, "");
        verify(offeringRepository).count();
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByPrice() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_price");
        when(offeringRepository.findOfferings(pageRequest, ""))
                .thenReturn(Flux.empty());
        when(offeringRepository.count()).thenReturn(Mono.just(2L));

        offeringService.getOfferings("", SortType.PRICE, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, "");
        verify(offeringRepository).count();
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByTitle() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_title");
        when(offeringRepository.findOfferings(pageRequest, ""))
                .thenReturn(Flux.empty());
        when(offeringRepository.count()).thenReturn(Mono.just(2L));

        offeringService.getOfferings("", SortType.ALPHA, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, "");
        verify(offeringRepository).count();
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_sortByPrice() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_price");
        String search = "Test";
        when(offeringRepository.findOfferings(pageRequest, search))
                .thenReturn(Flux.empty());
        when(offeringRepository.countByTitleContainingOrDescriptionContaining(search, search))
                .thenReturn(Mono.just(0));

        offeringService.getOfferings(search, SortType.PRICE, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, search);
        verify(offeringRepository).countByTitleContainingOrDescriptionContaining(search, search);
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_noSort() {
        PageRequest pageRequest = new PageRequest(1, 10);
        String search = "Test";
        when(offeringRepository.findOfferings(pageRequest, search))
                .thenReturn(Flux.empty());
        when(offeringRepository.countByTitleContainingOrDescriptionContaining(search, search))
                .thenReturn(Mono.just(0));

        offeringService.getOfferings(search, SortType.NO, 1, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, search);
        verify(offeringRepository).countByTitleContainingOrDescriptionContaining(search, search);
    }

    @Test
    public void createOfferingTest() {
        MockMultipartFile mockImage = new MockMultipartFile("image",
                "NewBalance.png",
                MediaType.IMAGE_PNG_VALUE,
                "Test image content".getBytes()
        );
        when(offeringRepository.save(any(Offering.class)))
                .thenAnswer(i -> {
                    Offering createdOffering = i.getArgument(0);
                    createdOffering.setId(10L);
                    return Mono.just(createdOffering);
                });

        offeringService.createOffering("Кроссовки",
                "Кроссовки New Balance",
                BigDecimal.valueOf(20000),
                mockImage
        ).block();
        verify(imageService).uploadImage(mockImage);
    }

    private OfferingWithCartItem generateTestOffering(long id) {
        return new OfferingWithCartItem(id,
                "Test", "Test description", "img.png", BigDecimal.valueOf(100L),
                10L, 3);
    }
}
