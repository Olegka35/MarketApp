package com.tarasov.market.controller;


import com.tarasov.market.model.dto.OfferingDto;
import com.tarasov.market.model.dto.OfferingPage;
import com.tarasov.market.model.dto.PageInfo;
import com.tarasov.market.model.dto.type.SortType;
import com.tarasov.market.service.OfferingService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class OfferingController {

    private final OfferingService offeringService;

    @GetMapping({ "/", "/items" })
    public String loadMainOfferingsPage(@RequestParam(defaultValue = "") String search,
                                        @RequestParam(defaultValue = "NO") SortType sort,
                                        @RequestParam(defaultValue = "1") @Positive int pageNumber,
                                        @RequestParam(defaultValue = "5") @Positive int pageSize,
                                        Model model) {
        OfferingPage page = offeringService.getOfferings(search, sort, pageNumber, pageSize);
        var offerings = groupOfferings(page.getOfferings(), 3);
        model.addAttribute("items", offerings);
        model.addAttribute("paging",
                new PageInfo(pageSize, pageNumber, pageNumber > 1, pageNumber < page.getTotalPages()));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort.name());
        return "items";
    }

    @GetMapping("/items/{id}")
    public String loadOfferingPage(@PathVariable @Positive long id,
                                   Model model) {
        model.addAttribute("item", offeringService.getOffering(id));
        return "item";
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
