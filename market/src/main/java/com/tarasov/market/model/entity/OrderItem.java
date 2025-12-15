package com.tarasov.market.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItem {
    @Id
    private Long id;
    private Long orderId;
    private Long offeringId;
    private int amount;
    private BigDecimal unitPrice;

    public OrderItem(Long orderId, Long offeringId, int amount, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.offeringId = offeringId;
        this.amount = amount;
        this.unitPrice = unitPrice;
    }
}
