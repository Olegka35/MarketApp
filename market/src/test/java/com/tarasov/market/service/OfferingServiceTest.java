package com.tarasov.market.service;


import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.entity.Offering;
import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingCacheRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.impl.OfferingServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Disabled
public class OfferingServiceTest {

    @InjectMocks
    private OfferingServiceImpl offeringService;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private OfferingCacheRepository offeringCacheRepository;

    @Mock
    private CartRepository cartRepository;

    @Test
    public void getOfferingByIdTest() {
        long ID = 5L;
        OfferingWithCartItem mockOffering = generateTestOffering(ID);

        when(offeringCacheRepository.findByOfferingId(anyLong()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOffering(any(OfferingCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));
        when(offeringRepository.findByIdWithCart(ID, 1L)).thenReturn(Mono.just(mockOffering));

        OfferingDto offeringDto = offeringService.getOffering(ID).block();

        assertNotNull(offeringDto);
        assertEquals(ID, offeringDto.id());
        assertEquals("Test", offeringDto.title());
        assertEquals("Test description", offeringDto.description());
        assertEquals("img.png", offeringDto.imgPath());
        assertEquals(BigDecimal.valueOf(100L), offeringDto.price());
        assertEquals(3, offeringDto.count());

        ArgumentCaptor<OfferingCache> offeringCacheArgumentCaptor = ArgumentCaptor.forClass(OfferingCache.class);
        verify(offeringCacheRepository).saveOffering(offeringCacheArgumentCaptor.capture());
        OfferingCache offeringCache = offeringCacheArgumentCaptor.getValue();
        assertEquals(ID, offeringCache.id());
        assertEquals("Test", offeringCache.title());
        assertEquals("Test description", offeringCache.description());
        assertEquals("img.png", offeringCache.imgPath());
        assertEquals(BigDecimal.valueOf(100L), offeringCache.price());
    }

    @Test
    public void getOfferingByIdTest_notFound() {
        long ID = 5L;
        when(offeringCacheRepository.findByOfferingId(anyLong())).thenReturn(Mono.empty());
        when(offeringRepository.findByIdWithCart(ID, 1L)).thenReturn(Mono.empty());

        assertThrows(NoSuchElementException.class, () -> offeringService.getOffering(ID).block());
    }

    @Test
    public void searchOfferingsTest_checkResponseProcessing() {
        PageRequest pageRequest = new PageRequest(1, 5);
        when(offeringCacheRepository.findOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt(), any(OfferingPageCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));
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

        ArgumentCaptor<OfferingPageCache> offeringCacheArgumentCaptor = ArgumentCaptor.forClass(OfferingPageCache.class);
        verify(offeringCacheRepository)
                .saveOfferingPage(eq(""), eq(SortType.NO), eq(1), eq(5), offeringCacheArgumentCaptor.capture());
        OfferingPageCache offeringPage = offeringCacheArgumentCaptor.getValue();
        assertEquals(1, offeringPage.totalPages());
        assertEquals(2, offeringPage.offerings().size());
        OfferingCache offeringDto = offeringPage.offerings().getLast();
        assertEquals("Test", offeringDto.title());
        assertEquals("Test description", offeringDto.description());
        assertEquals("img.png", offeringDto.imgPath());
        assertEquals(BigDecimal.valueOf(100L), offeringDto.price());
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByPrice() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_price");
        when(offeringCacheRepository.findOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt(), any(OfferingPageCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));
        when(offeringRepository.findOfferings(pageRequest, ""))
                .thenReturn(Flux.empty());
        when(offeringRepository.count()).thenReturn(Mono.just(2L));

        offeringService.getOfferings("", SortType.PRICE, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, "");
        verify(offeringRepository).count();
        verify(offeringCacheRepository)
                .saveOfferingPage(eq(""), eq(SortType.PRICE), eq(2), eq(10), any(OfferingPageCache.class));
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_emptyString_sortByTitle() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_title");
        when(offeringRepository.findOfferings(pageRequest, ""))
                .thenReturn(Flux.empty());
        when(offeringRepository.count()).thenReturn(Mono.just(2L));
        when(offeringCacheRepository.findOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt(), any(OfferingPageCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        offeringService.getOfferings("", SortType.ALPHA, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, "");
        verify(offeringRepository).count();
        verify(offeringCacheRepository)
                .saveOfferingPage(eq(""), eq(SortType.ALPHA), eq(2), eq(10), any(OfferingPageCache.class));
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_sortByPrice() {
        PageRequest pageRequest = new PageRequest(2, 10, "offering_price");
        String search = "Test";
        when(offeringCacheRepository.findOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt(), any(OfferingPageCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));
        when(offeringRepository.findOfferings(pageRequest, search))
                .thenReturn(Flux.empty());
        when(offeringRepository.countByTitleContainingOrDescriptionContaining(search, search))
                .thenReturn(Mono.just(0));

        offeringService.getOfferings(search, SortType.PRICE, 2, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, search);
        verify(offeringRepository).countByTitleContainingOrDescriptionContaining(search, search);
        verify(offeringCacheRepository)
                .saveOfferingPage(eq(search), eq(SortType.PRICE), eq(2), eq(10), any(OfferingPageCache.class));
    }

    @Test
    public void searchOfferingsTest_checkSearchParametersProcessing_noSort() {
        PageRequest pageRequest = new PageRequest(1, 10);
        String search = "Test";
        when(offeringRepository.findOfferings(pageRequest, search))
                .thenReturn(Flux.empty());
        when(offeringRepository.countByTitleContainingOrDescriptionContaining(search, search))
                .thenReturn(Mono.just(0));
        when(offeringCacheRepository.findOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(offeringCacheRepository.saveOfferingPage(anyString(), any(SortType.class), anyInt(), anyInt(), any(OfferingPageCache.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        offeringService.getOfferings(search, SortType.NO, 1, 10).block();

        verify(offeringRepository).findOfferings(pageRequest, search);
        verify(offeringRepository).countByTitleContainingOrDescriptionContaining(search, search);
        verify(offeringCacheRepository)
                .saveOfferingPage(eq(search), eq(SortType.NO), eq(1), eq(10), any(OfferingPageCache.class));
    }

    @Test
    public void createOfferingTest() {
        FilePart mockImage = Mockito.mock(FilePart.class);
        when(mockImage.filename()).thenReturn("NewBalance.png");
        when(imageService.uploadImage(mockImage)).thenReturn(Mono.empty());
        when(offeringRepository.save(any(Offering.class)))
                .thenAnswer(i -> {
                    Offering createdOffering = i.getArgument(0);
                    createdOffering.setId(10L);
                    return Mono.just(createdOffering);
                });
        when(offeringCacheRepository.clearCache()).thenReturn(Mono.just(3L));

        offeringService.createOffering("Кроссовки",
                "Кроссовки New Balance",
                BigDecimal.valueOf(20000),
                mockImage
        ).block();
        verify(imageService).uploadImage(mockImage);
        verify(offeringCacheRepository).clearCache();
    }

    @Test
    public void getOfferingFromCacheTest_notInCart() {
        long ID = 5L;
        OfferingCache mockOffering = new OfferingCache(ID,
                "Laptop",
                "Lenovo Legion 5 Pro",
                "legion.img",
                BigDecimal.valueOf(500));

        when(offeringCacheRepository.findByOfferingId(anyLong())).thenReturn(Mono.just(mockOffering));
        when(cartRepository.findByOfferingIdAndUserId(anyLong(), anyLong())).thenReturn(Mono.empty());
        when(offeringRepository.findByIdWithCart(ID, 1L)).thenReturn(Mono.empty());

        OfferingDto offeringDto = offeringService.getOffering(ID).block();

        assertNotNull(offeringDto);
        assertEquals(ID, offeringDto.id());
        assertEquals("Laptop", offeringDto.title());
        assertEquals("Lenovo Legion 5 Pro", offeringDto.description());
        assertEquals("legion.img", offeringDto.imgPath());
        assertEquals(BigDecimal.valueOf(500L), offeringDto.price());
        assertEquals(0, offeringDto.count());
    }

    @Test
    public void getOfferingFromCacheTest_inCart() {
        long ID = 5L;
        OfferingCache mockOffering = new OfferingCache(ID,
                "Laptop",
                "Lenovo Legion 5 Pro",
                "legion.img",
                BigDecimal.valueOf(500));

        when(offeringCacheRepository.findByOfferingId(ID)).thenReturn(Mono.just(mockOffering));
        when(cartRepository.findByOfferingIdAndUserId(ID, 1L)).thenReturn(Mono.just(new CartItem(ID, 5, 1L)));
        when(offeringRepository.findByIdWithCart(ID, 1L)).thenReturn(Mono.empty());

        OfferingDto offeringDto = offeringService.getOffering(ID).block();

        assertNotNull(offeringDto);
        assertEquals(ID, offeringDto.id());
        assertEquals("Laptop", offeringDto.title());
        assertEquals("Lenovo Legion 5 Pro", offeringDto.description());
        assertEquals("legion.img", offeringDto.imgPath());
        assertEquals(BigDecimal.valueOf(500L), offeringDto.price());
        assertEquals(5, offeringDto.count());
    }

    @Test
    public void searchOfferingsInCacheTest_checkResponseProcessing() {
        PageRequest pageRequest = new PageRequest(1, 5);
        when(offeringCacheRepository.findOfferingPage("", SortType.NO, 1, 5))
                .thenReturn(Mono.just(new OfferingPageCache(2,
                        List.of(
                                new OfferingCache(2L, "Test Offering", "Description", "offering.img", BigDecimal.valueOf(100)),
                                new OfferingCache(4L, "Laptop", "Lenovo Legion 5 Pro", "legion.img", BigDecimal.valueOf(500))
                        )
                )));
        when(cartRepository.findByOfferingIdInAndUserId(List.of(2L, 4L), 1L))
                .thenReturn(Flux.just(new CartItem(4L, 5, 1L)));
        when(offeringRepository.findOfferings(pageRequest, "")).thenReturn(Flux.empty());
        when(offeringRepository.count()).thenReturn(Mono.empty());

        OfferingPage page = offeringService.getOfferings("", SortType.NO, 1, 5).block();

        assertNotNull(page);
        assertEquals(2, page.getOfferings().size());
        OfferingDto lastOffering = page.getOfferings().getLast();
        assertEquals("Laptop", lastOffering.title());
        assertEquals("Lenovo Legion 5 Pro", lastOffering.description());
        assertEquals("legion.img", lastOffering.imgPath());
        assertEquals(BigDecimal.valueOf(500L), lastOffering.price());
        assertEquals(5, lastOffering.count());
        assertEquals(0, page.getOfferings().getFirst().count());
        assertEquals(2, page.getTotalPages());
    }

    private OfferingWithCartItem generateTestOffering(long id) {
        return new OfferingWithCartItem(id,
                "Test", "Test description", "img.png", BigDecimal.valueOf(100L),
                10L, 3);
    }
}
