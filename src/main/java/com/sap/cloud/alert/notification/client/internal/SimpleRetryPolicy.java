package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IRetryPolicy;

import java.time.Duration;
import java.util.function.Supplier;

import static java.lang.System.currentTimeMillis;

public class SimpleRetryPolicy implements IRetryPolicy {

    private final int maxRetries;
    private final Duration retryBackoff;

    public SimpleRetryPolicy() {
        this(0, Duration.ZERO);
    }

    public SimpleRetryPolicy(int maxRetries, Duration retryBackoff) {
        this.maxRetries = maxRetries;
        this.retryBackoff = retryBackoff;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    @Override
    public <T> T executeWithRetry(Supplier<T> supplier) {
        for (int i = 0; i < maxRetries; ++i) {
            try {
                return supplier.get();
            } catch (Exception exception) {
                sleepAtLeast(retryBackoff);
            }
        }

        return supplier.get();
    }

    public static void sleepAtLeast(Duration duration) {
        long sleepUntil = currentTimeMillis() + duration.toMillis();

        while (currentTimeMillis() < sleepUntil) {
            trySleep(Duration.ofMillis(sleepUntil - currentTimeMillis()));
        }
    }

    private static void trySleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            // Nothing to be done, ignore.
        }
    }
}
