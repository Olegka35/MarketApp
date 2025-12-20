package com.tarasov.market.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@AllArgsConstructor
public class User {

    @Id
    private Long id;
    private String username;
    private String password;
    private boolean isAdmin;
    private boolean enabled;
}
