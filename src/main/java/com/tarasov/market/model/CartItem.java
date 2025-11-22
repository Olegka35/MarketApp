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

    private Offering offering;
    private int amount;

    public CartItem(Offering offering, int amount) {
        this.offering = offering;
        this.amount = amount;
    }
}
