package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectUtilsTest {

    // requireNonNull

    @Test
    void givenNonNullValue_whenRequireNonNullIsCalled_thenValueIsReturned() {
        assertEquals("value", ObjectUtils.requireNonNull("value", "must not be null"));
    }

    @Test
    void givenNullValue_whenRequireNonNullIsCalled_thenExceptionIsThrown() {
        ClientRequestException thrown = assertThrows(ClientRequestException.class,
                () -> ObjectUtils.requireNonNull(null, "must not be null"));
        assertEquals("must not be null", thrown.getMessage());
    }

    // requirePositive (Duration)

    @ParameterizedTest
    @MethodSource("positiveDurations")
    void givenPositiveDuration_whenRequirePositiveIsCalled_thenValueIsReturned(Duration duration) {
        assertEquals(duration, ObjectUtils.requirePositive(duration, "must be positive"));
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void givenNonPositiveDuration_whenRequirePositiveIsCalled_thenExceptionIsThrown(Duration duration) {
        ClientRequestException thrown = assertThrows(ClientRequestException.class,
                () -> ObjectUtils.requirePositive(duration, "must be positive"));
        assertEquals("must be positive", thrown.getMessage());
    }

    static Stream<Duration> positiveDurations() {
        return Stream.of(Duration.ofMillis(1), Duration.ofSeconds(30), Duration.ofHours(1));
    }

    static Stream<Duration> nonPositiveDurations() {
        return Stream.of(null, Duration.ZERO, Duration.ofSeconds(-1));
    }
}
