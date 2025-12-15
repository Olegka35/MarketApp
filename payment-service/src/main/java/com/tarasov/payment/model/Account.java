package com.tarasov.payment.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("accounts")
@Data
@AllArgsConstructor
public class Account {

    @Id
    private Long id;
    private BigDecimal amount;
}
