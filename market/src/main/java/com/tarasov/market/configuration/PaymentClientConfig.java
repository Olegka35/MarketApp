package com.tarasov.market.configuration;

import com.tarasov.market.api.PaymentApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi();
    }
}
