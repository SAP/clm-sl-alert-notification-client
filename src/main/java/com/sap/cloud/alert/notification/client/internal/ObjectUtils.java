package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public class ObjectUtils {

    public static <T> T requireNonNull(T object, String message) {
        throwExceptionIf(() -> object == null, message);
        return object;
    }

    public static Duration requirePositive(Duration duration, String message) {
        throwExceptionIf(() -> duration == null || duration.isZero() || duration.isNegative(), message);
        return duration;
    }

    private static void throwExceptionIf(BooleanSupplier booleanSupplier, String message) {
        if (booleanSupplier.getAsBoolean()) {
            throw new ClientRequestException(message);
        }
    }
}
