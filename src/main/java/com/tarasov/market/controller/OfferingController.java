package com.tarasov.market.controller;


import com.tarasov.market.model.dto.PageInfo;
import com.tarasov.market.model.dto.SortType;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OfferingController {

    @GetMapping({ "/", "/items" })
    public String loadMainOfferingsPage(@RequestParam(defaultValue = "") String search,
                                        @RequestParam(defaultValue = "NO") SortType sort,
                                        @RequestParam(defaultValue = "1") @Positive int pageNumber,
                                        @RequestParam(defaultValue = "5") @Positive int pageSize,
                                        Model model) {
        model.addAttribute("paging",
                new PageInfo(10, 1, false, false));
        return "items";
    }

    @GetMapping("/items/{id}")
    public String loadOfferingPage(@PathVariable @Positive long id,
                                   Model model) {
        model.addAttribute("paging",
                new PageInfo(10, 1, false, false));
        return "item";
    }
}
