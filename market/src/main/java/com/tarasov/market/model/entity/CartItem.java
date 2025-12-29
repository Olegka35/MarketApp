package com.tarasov.market.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "cart")
@Data
@NoArgsConstructor
public class CartItem {

    @Id
    private Long id;

    private Long offeringId;
    private int amount;
    private Long userId;

    public CartItem(Long offeringId, int amount, Long userId) {
        this.offeringId = offeringId;
        this.amount = amount;
        this.userId = userId;
    }
}
