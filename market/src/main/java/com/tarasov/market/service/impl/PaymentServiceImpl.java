package com.tarasov.market.service.impl;


import com.tarasov.market.api.PaymentApi;
import com.tarasov.market.model.BalanceInfo;
import com.tarasov.market.model.PaymentRequest;
import com.tarasov.market.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentApi paymentApi;
    private final Long accountId;

    public PaymentServiceImpl(PaymentApi paymentApi,
                              @Value("${payment.service.account-id}") Long accountId) {
        if (System.getenv("PAYMENT_SERVICE_URL") != null) {
            paymentApi.getApiClient().setBasePath(System.getenv("PAYMENT_SERVICE_URL"));
        }
        this.paymentApi = paymentApi;
        this.accountId = accountId;
    }

    @Override
    public Mono<BigDecimal> getAccountBalance() {
        return paymentApi.getAccountById(accountId)
                .map(BalanceInfo::getBalance);
    }

    @Override
    public Mono<BigDecimal> makePayment(BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest().amount(amount);
        return paymentApi.makePayment(accountId, paymentRequest)
                .map(BalanceInfo::getBalance);
    }
}
