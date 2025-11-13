package com.tarasov.market;


import com.tarasov.market.configuration.PostgresTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
public class MarketAppApplicationTest {

    @Test
    void contextLoads() {}
}
