package com.tarasov.payment.configuration;

import com.tarasov.payment.repository.AccountRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;


@Component
public class BalanceResetExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        processTestMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<@Nullable Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        processTestMethod(invocation, invocationContext, extensionContext);
    }

    private void processTestMethod(Invocation<Void> invocation,
                                   ReflectiveInvocationContext<Method> invocationContext,
                                   ExtensionContext extensionContext) throws Throwable {
        Method testMethod = invocationContext.getExecutable();
        if (!testMethod.isAnnotationPresent(ResetBalance.class)) {
            invocation.proceed();
            return;
        }
        AccountRepository accountRepository = SpringExtension.getApplicationContext(extensionContext)
                .getBean(AccountRepository.class);
        try {
            invocation.proceed();
        } finally {
            resetBalances(accountRepository);
        }
    }

    private void resetBalances(AccountRepository accountRepository) throws IOException {
        accountRepository.findAll()
                .map(account -> {
                    account.setAmount(BigDecimal.valueOf(5000));
                    return account;
                })
                .as(accountRepository::saveAll)
                .then()
                .block();
    }
}