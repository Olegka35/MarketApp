package com.tarasov.market.configuration;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientConfiguration {

    @Bean
    WebClient defaultWebClient() {
        return WebClient.builder().build();
    }
}
