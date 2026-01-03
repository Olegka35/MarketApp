package com.tarasov.market.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @ReadOnlyProperty
    private Long id;

    @ReadOnlyProperty
    private LocalDateTime createdDate;
    private BigDecimal totalPrice;
    private Long userId;
}
