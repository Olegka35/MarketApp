package com.tarasov.market.service;


import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.type.SortType;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.impl.OfferingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        Offering mockOffering = generateTestOffering(ID);
        when(offeringRepository.findById(ID)).thenReturn(Optional.of(mockOffering));

        OfferingDto offeringDto = offeringService.getOffering(ID);

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
        when(offeringRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> offeringService.getOffering(ID));
    }

    @Test
    public void searchOfferingsTest_checkResponseProcessing() {
        Page<Offering> offeringPage = new PageImpl<>(List.of(generateTestOffering(1L), generateTestOffering(4L)));
        when(offeringRepository.findAllWithCart(PageRequest.of(0, 5)))
                .thenReturn(offeringPage);

        OfferingPage page = offeringService.getOfferings("", SortType.NO, 1, 5);
        assertEquals(2, page.getOfferings().size());
        OfferingDto lastOffering = page.getOfferings().getLast();
        assertEquals("Test", lastOffering.title());
        assertEquals("Test description", lastOffering.description());
        assertEquals("img.png", lastOffering.imgPath());
        assertEquals(BigDecimal.valueOf(100L), lastOffering.price());
        assertEquals(3, lastOffering.count());
        assertEquals(1, page.getTotalPages());

        verify(offeringRepository).findAllWithCart(PageRequest.of(0, 5));
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByPrice() {
        PageRequest pageRequest = PageRequest.of(1, 10)
                .withSort(Sort.by(Sort.Direction.ASC, "price"));
        when(offeringRepository.findAllWithCart(pageRequest)).thenReturn(new PageImpl<>(List.of()));

        offeringService.getOfferings("", SortType.PRICE, 2, 10);

        verify(offeringRepository).findAllWithCart(pageRequest);
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByTitle() {
        PageRequest pageRequest = PageRequest.of(1, 10)
                .withSort(Sort.by(Sort.Direction.ASC, "title"));
        when(offeringRepository.findAllWithCart(pageRequest)).thenReturn(new PageImpl<>(List.of()));

        offeringService.getOfferings("", SortType.ALPHA, 2, 10);

        verify(offeringRepository).findAllWithCart(pageRequest);
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_sortByPrice() {
        PageRequest pageRequest = PageRequest.of(1, 10)
                .withSort(Sort.by(Sort.Direction.ASC, "price"));
        String search = "Test";
        when(offeringRepository.findByTitleContainsOrDescriptionContains(search, search, pageRequest))
                .thenReturn(new PageImpl<>(List.of()));

        offeringService.getOfferings(search, SortType.PRICE, 2, 10);

        verify(offeringRepository).findByTitleContainsOrDescriptionContains(search, search, pageRequest);
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_noSort() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        String search = "Test";
        when(offeringRepository.findByTitleContainsOrDescriptionContains(search, search, pageRequest))
                .thenReturn(new PageImpl<>(List.of()));

        offeringService.getOfferings(search, SortType.NO, 2, 10);

        verify(offeringRepository).findByTitleContainsOrDescriptionContains(search, search, pageRequest);
    }

    @Test
    public void createOfferingTest() {
        MockMultipartFile mockImage = new MockMultipartFile("image",
                "NewBalance.png",
                MediaType.IMAGE_PNG_VALUE,
                "Test image content".getBytes()
        );
        when(offeringRepository.save(any(Offering.class))).thenAnswer(i -> i.getArgument(0));

        offeringService.createOffering("Кроссовки",
                "Кроссовки New Balance",
                BigDecimal.valueOf(20000),
                mockImage
        );
        verify(imageService).uploadImage(mockImage);
    }

    private Offering generateTestOffering(long id) {
        Offering offering = new Offering("Test", "Test description", "img.png", BigDecimal.valueOf(100L));
        offering.setId(id);
        offering.setCartItem(new CartItem(offering, 3));
        return offering;
    }
}
