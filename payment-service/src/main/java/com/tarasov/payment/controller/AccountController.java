package com.tarasov.payment.controller;


import com.tarasov.payment.model.dto.BalanceDto;
import com.tarasov.payment.model.dto.PaymentRequest;
import com.tarasov.payment.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/{id}")
    public Mono<BalanceDto> getAccountBalance(@PathVariable @Positive Long id) {
        return accountService.getAccountBalance(id)
                .map(BalanceDto::new);
    }

    @PostMapping("/account/{id}/payment")
    public Mono<BalanceDto> makePayment(@PathVariable @Positive Long id,
                                        @RequestBody @Valid PaymentRequest paymentRequest) {
        return accountService.deductBalance(id, paymentRequest.amount())
                .map(BalanceDto::new);
    }
}
