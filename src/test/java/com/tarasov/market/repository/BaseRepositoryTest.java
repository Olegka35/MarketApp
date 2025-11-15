package com.tarasov.market.repository;


import com.tarasov.market.configuration.PostgresTestcontainer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest(properties = {
        "spring.sql.init.mode=never"
})
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
public abstract class BaseRepositoryTest {
}
