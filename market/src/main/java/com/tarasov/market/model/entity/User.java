package com.tarasov.market.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long id;
    private String username;
    private String password;
    private boolean isAdmin;
    private boolean enabled;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isAdmin = false;
        this.enabled = true;
    }
}
