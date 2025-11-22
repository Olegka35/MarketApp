package com.tarasov.market.model;


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

    public CartItem(Long offeringId, int amount) {
        this.offeringId = offeringId;
        this.amount = amount;
    }
}
