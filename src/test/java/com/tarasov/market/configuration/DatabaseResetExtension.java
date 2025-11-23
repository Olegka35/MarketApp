package com.tarasov.market.configuration;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;


@Component
public class DatabaseResetExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        Method testMethod = invocationContext.getExecutable();
        if (!testMethod.isAnnotationPresent(ResetDB.class)) {
            invocation.proceed();
            return;
        }
        DatabaseClient databaseClient = SpringExtension.getApplicationContext(extensionContext)
                .getBean(DatabaseClient.class);
        try {
            invocation.proceed();
        } finally {
            resetDB(databaseClient);
        }
    }

    private void resetDB(DatabaseClient databaseClient) throws IOException {
        Path resetDbScriptPath = Path.of("src/test/resources/reset_db.sql");
        Path dataScriptPath = Path.of("src/test/resources/data.sql");
        if (!Files.exists(resetDbScriptPath) ||  !Files.exists(dataScriptPath)) {
            throw new NoSuchFileException("Reset DB scripts not found");
        }
        executeScript(databaseClient, resetDbScriptPath);
        executeScript(databaseClient, dataScriptPath);
    }

    private void executeScript(DatabaseClient databaseClient, Path scriptPath)
            throws IOException {
        databaseClient.sql(Files.readString(scriptPath))
                .fetch()
                .rowsUpdated()
                .block();
    }

}
