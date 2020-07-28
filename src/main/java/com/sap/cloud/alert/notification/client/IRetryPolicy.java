package com.sap.cloud.alert.notification.client;

import java.util.function.Supplier;

public interface IRetryPolicy {

    /**
     * Executes a predefined expression with retries
     * according to the specific retry policy implementation
     *
     * @param supplier is the expression to be executed
     * @return the result of the execution of the supplier
     */
    <T> T executeWithRetry(Supplier<T> supplier);
}
