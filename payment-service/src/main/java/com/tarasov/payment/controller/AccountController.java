package com.tarasov.payment.controller;

import com.tarasov.payment.model.BalanceInfo;
import com.tarasov.payment.model.PaymentRequest;
import com.tarasov.payment.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Validated
public class AccountController implements AccountApi {

    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<BalanceInfo>> getAccountById(Long id,
                                                            ServerWebExchange exchange) {
        return accountService.getAccountBalance(id)
                .map(BalanceInfo::new)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BalanceInfo>> makePayment(Long id,
                                                         Mono<PaymentRequest> paymentRequest,
                                                         ServerWebExchange exchange) {
        return paymentRequest
                .flatMap(request -> accountService.deductBalance(id, request.getAmount()))
                .map(BalanceInfo::new)
                .map(ResponseEntity::ok);
    }
}
