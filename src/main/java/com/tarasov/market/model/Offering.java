package com.tarasov.market.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "offerings")
@Data
@NoArgsConstructor
public class Offering {

    @Id
    private Long id;

    private String title;
    private String description;
    private String imgPath;
    private BigDecimal price;

    public Offering(String title, String description, String imgPath, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
    }
}
