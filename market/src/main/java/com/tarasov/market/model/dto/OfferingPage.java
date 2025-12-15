package com.tarasov.market.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class OfferingPage {
    private List<OfferingDto> offerings;
    private int totalPages;
}
