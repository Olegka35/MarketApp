package com.tarasov.market.controller;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.PageInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OfferingControllerTest extends BaseControllerTest {

    @Test
    public void searchOfferingsTest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "paging", "sort", "search"))
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(result -> {
                    List<List<OfferingDto>> items = (List<List<OfferingDto>>) result.getModelAndView().getModel().get("items");
                    assertEquals(3, items.get(0).size());
                    assertEquals(3, items.get(1).size());
                    assertEquals(-1, items.get(1).get(2).id());

                    PageInfo page = (PageInfo) result.getModelAndView().getModel().get("paging");
                    assertEquals(1, page.pageNumber());
                    assertEquals(5, page.pageSize());
                    assertFalse(page.hasPrevious());
                    assertFalse(page.hasNext());
                })
                .andExpect(model().attribute("sort", "NO"));
    }

    @Test
    public void searchOfferingsWithFilterTest() throws Exception {
        mockMvc.perform(get("/items")
                        .param("search", " и "))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "paging", "sort", "search"))
                .andExpect(model().attribute("items", hasSize(1)))
                .andExpect(model().attribute("sort", "NO"))
                .andExpect(result -> {
                    PageInfo page = (PageInfo) result.getModelAndView().getModel().get("paging");
                    assertEquals(1, page.pageNumber());
                    assertEquals(5, page.pageSize());
                    assertFalse(page.hasPrevious());
                    assertFalse(page.hasNext());
                });
    }

    @Test
    public void searchOfferingsWithSortingAndPaginationTest() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "PRICE")
                        .param("pageNumber", "1")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "paging", "sort", "search"))
                .andExpect(model().attribute("items", hasSize(1)))
                .andExpect(model().attribute("sort", "PRICE"))
                .andExpect(result -> {
                    PageInfo page = (PageInfo) result.getModelAndView().getModel().get("paging");
                    assertEquals(1, page.pageNumber());
                    assertEquals(3, page.pageSize());
                    assertFalse(page.hasPrevious());
                    assertTrue(page.hasNext());
                });
    }

    @Test
    public void searchOfferingsTest_noResults() throws Exception {
        mockMvc.perform(get("/items")
                        .param("search", "INCORRECT_SEARCH"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items", "paging", "sort", "search"))
                .andExpect(model().attribute("items", hasSize(0)));
    }

    @ParameterizedTest
    @CsvSource({
            "NO, 0, 1",
            ", 1, 0",
            "PRICE, -1, 1",
            "ALPHA, 1, -1",
            "TEXT, 1, 5"
    })
    public void searchOfferingsTest_incorrectParams(String sort, String pageNumber, String pageSize) throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", sort)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOfferingByIdTest_addedToCart() throws Exception {
        mockMvc.perform(get("/items/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(result -> {
                    OfferingDto offeringDto = (OfferingDto) result.getModelAndView().getModel().get("item");
                    assertEquals(2L, offeringDto.id());
                    assertEquals("Беспроводная мышь", offeringDto.title());
                    assertEquals("Эргономичная мышь с Bluetooth-подключением и регулируемым DPI", offeringDto.description());
                    assertEquals("/wireless_mouse.jpg", offeringDto.imgPath());
                    assertEquals(BigDecimal.valueOf(990), offeringDto.price());
                    assertEquals(2, offeringDto.count());
                });
    }

    @Test
    public void getOfferingByIdTest_notInCart() throws Exception {
        mockMvc.perform(get("/items/3"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(result -> {
                    OfferingDto offeringDto = (OfferingDto) result.getModelAndView().getModel().get("item");
                    assertEquals(3L, offeringDto.id());
                    assertEquals("Рюкзак городской", offeringDto.title());
                    assertEquals("Лёгкий водоотталкивающий рюкзак с отделением для ноутбука 15.6\"", offeringDto.description());
                    assertEquals("/backpack.jpg", offeringDto.imgPath());
                    assertEquals(BigDecimal.valueOf(2790), offeringDto.price());
                    assertEquals(0, offeringDto.count());
                });
    }

    @Test
    public void getOfferingByIdTest_nonExistingOffering() throws Exception {
        mockMvc.perform(get("/items/300"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void createNewOfferingest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image",
                "NewBalance.png",
                MediaType.IMAGE_PNG_VALUE,
                "Test image content".getBytes()
        );
        mockMvc.perform(multipart("/items/new")
                        .file(image)
                        .param("title", "test")
                        .param("description", "test description")
                        .param("price", "1000"))
                .andExpect(status().is3xxRedirection());
    }
}
