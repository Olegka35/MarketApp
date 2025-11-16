package com.tarasov.market.controller;


import com.tarasov.market.configuration.PostgresTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
public class BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void contextLoads() {
    }
}
