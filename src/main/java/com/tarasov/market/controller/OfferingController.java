package com.tarasov.market.controller;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.PageInfo;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.service.OfferingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class OfferingController {

    private final OfferingService offeringService;

    @GetMapping({"/", "/items"})
    public Mono<Rendering> loadMainOfferingsPage(@RequestParam(defaultValue = "") String search,
                                                 @RequestParam(defaultValue = "NO") SortType sort,
                                                 @RequestParam(defaultValue = "1") @Positive int pageNumber,
                                                 @RequestParam(defaultValue = "5") @Positive int pageSize) {
        return offeringService.getOfferings(search, sort, pageNumber, pageSize)
                .map(page ->
                        Rendering.view("items")
                                .modelAttribute("items", groupOfferings(page.getOfferings(), 3))
                                .modelAttribute("paging", new PageInfo(
                                        pageSize,
                                        pageNumber,
                                        pageNumber > 1,
                                        pageNumber < page.getTotalPages()
                                ))
                                .modelAttribute("search", search)
                                .modelAttribute("sort", sort.name())
                                .build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> loadOfferingPage(@PathVariable @Positive long id) {
        Rendering rendering = Rendering.view("item")
                .modelAttribute("item", offeringService.getOffering(id))
                .build();
        return Mono.just(rendering);
    }

    @GetMapping("/items/new")
    public Mono<Rendering> openNewOfferingPage() {
        return Mono.just(Rendering.view("new_item").build());
    }

    @PostMapping("/items/new")
    public Mono<Rendering> createNewOffering(@RequestPart @NotBlank String title,
                                             @RequestPart @NotBlank String description,
                                             @RequestPart @Positive String price,
                                             @RequestPart Mono<FilePart> image) {
        System.out.println("Creating New Offering");
        return image.flatMap(filePart -> offeringService.createOffering(title, description, new BigDecimal(price), filePart)
                .map(newOfferingId ->
                        Rendering.redirectTo(String.format("/items/%d", newOfferingId)).build()));
    }

    private List<? extends List<OfferingDto>> groupOfferings(List<OfferingDto> offerings, int rowSize) {
        var list = IntStream.range(0, (offerings.size() + rowSize - 1) / rowSize)
                .mapToObj(i ->
                        new ArrayList<>(offerings.subList(i * rowSize, Math.min(offerings.size(), (i + 1) * rowSize))))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        for (int i = list.getLast().size(); i < rowSize; i++) {
            OfferingDto dummyOffering = OfferingDto.dummyOffering();
            list.getLast().add(dummyOffering);
        }
        return list;
    }
}
