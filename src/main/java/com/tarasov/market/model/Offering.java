package com.tarasov.market.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "offerings")
@Data
@NoArgsConstructor
public class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imgPath;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToOne(mappedBy = "offering")
    private CartItem cartItem;

    public Offering(String title, String description, String imgPath, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
    }
}
