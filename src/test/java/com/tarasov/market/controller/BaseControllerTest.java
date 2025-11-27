package com.tarasov.market.controller;


import com.tarasov.market.configuration.DatabaseResetExtension;
import com.tarasov.market.configuration.PostgresTestcontainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
@ExtendWith(DatabaseResetExtension.class)
public class BaseControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void contextLoads() {
    }
}
