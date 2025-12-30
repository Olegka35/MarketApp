package com.tarasov.market.service.impl;


import com.tarasov.market.ApiClient;
import com.tarasov.market.api.PaymentApi;
import com.tarasov.market.model.BalanceInfo;
import com.tarasov.market.model.PaymentRequest;
import com.tarasov.market.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentApi paymentApi;

    public PaymentServiceImpl(PaymentApi paymentApi,
                              WebClient webClient) {
        ApiClient apiClient = new ApiClient(webClient);
        if (System.getenv("PAYMENT_SERVICE_URL") != null) {
            apiClient.setBasePath(System.getenv("PAYMENT_SERVICE_URL"));
        }
        paymentApi.setApiClient(apiClient);
        this.paymentApi = paymentApi;
    }

    @Override
    public Mono<BigDecimal> getAccountBalance(Long accountId) {
        return paymentApi.getAccountById(accountId)
                .map(BalanceInfo::getBalance);
    }

    @Override
    public Mono<BigDecimal> makePayment(Long accountId, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest().amount(amount);
        return paymentApi.makePayment(accountId, paymentRequest)
                .map(BalanceInfo::getBalance);
    }
}
