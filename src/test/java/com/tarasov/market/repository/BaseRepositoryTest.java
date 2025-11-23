package com.tarasov.market.repository;


import com.tarasov.market.configuration.PostgresTestcontainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
public abstract class BaseRepositoryTest {
}
