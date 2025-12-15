package com.tarasov.market.repository;


import com.tarasov.market.configuration.PostgresTestcontainer;
import com.tarasov.market.configuration.DatabaseResetExtension;
import com.tarasov.market.configuration.RedisTestcontainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ImportTestcontainers( { PostgresTestcontainer.class, RedisTestcontainer.class } )
@ExtendWith(DatabaseResetExtension.class)
public abstract class BaseRepositoryTest {
}
