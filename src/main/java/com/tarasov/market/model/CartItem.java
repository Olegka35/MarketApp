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
    @Column(name = "offering_id")
    private Long offeringId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "offering_id", referencedColumnName = "id")
    private Offering offering;

    @Column(nullable = false)
    private int amount;
}
