package com.tarasov.market.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Table("offerings")
public class Offering {
    @Id
    private BigInteger id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String imgPath;

    @NonNull
    private Long price;
}
