package com.tarasov.market.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "offering_id", referencedColumnName = "id")
    private Offering offering;

    @Column(nullable = false)
    private int amount;

    public CartItem(Offering offering, int amount) {
        this.offering = offering;
        this.amount = amount;
    }
}
