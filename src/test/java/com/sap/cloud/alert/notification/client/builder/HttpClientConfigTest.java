package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpClientConfigTest {

    private static final HttpClientConfig VALID_CONFIG = HttpClientConfig.fromDefaultConfig() //
            .connectionObtainTimeout(Duration.ofSeconds(10)) //
            .connectionEstablishTimeout(Duration.ofSeconds(11)) //
            .socketTimeout(Duration.of(12, ChronoUnit.SECONDS)) //
            .build();

    @Test
    void givenValidParameters_whenConfigIsBuilt_thenCorrectConfigReturned() {
        assertEquals(Duration.ofSeconds(10), VALID_CONFIG.getConnectionObtainTimeout());
        assertEquals(Duration.ofSeconds(11), VALID_CONFIG.getConnectionEstablishTimeout());
        assertEquals(Duration.ofSeconds(12), VALID_CONFIG.getSocketTimeout());
    }

    @Test
    void whenFromDefaultConfigIsCalled_thenCorrectConfigReturned() {
        HttpClientConfig config = HttpClientConfig.fromDefaultConfig().build();

        assertEquals(Duration.ofSeconds(30), config.getConnectionObtainTimeout());
        assertEquals(Duration.ofSeconds(30), config.getConnectionEstablishTimeout());
        assertEquals(Duration.ofSeconds(30), config.getSocketTimeout());
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void givenNonPositiveConnectionObtainTimeout_whenConfigIsBuilt_thenExceptionIsThrown(Duration duration) {
        assertThrown(() -> VALID_CONFIG.toBuilder().connectionObtainTimeout(duration).build(), "connectionObtainTimeout must be positive duration");
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void givenNonPositiveConnectionEstablishTimeout_whenConfigIsBuilt_thenExceptionIsThrown(Duration duration) {
        assertThrown(() -> VALID_CONFIG.toBuilder().connectionEstablishTimeout(duration).build(), "connectionEstablishTimeout must be positive duration");
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void givenNonPositiveSocketTimeout_whenConfigIsBuilt_thenExceptionIsThrown(Duration duration) {
        assertThrown(() -> VALID_CONFIG.toBuilder().socketTimeout(duration).build(), "socketTimeout must be positive duration");
    }

    public static Stream<Duration> nonPositiveDurations() {
        return Stream.of(null, Duration.ZERO, Duration.ofSeconds(-1));
    }

    private static void assertThrown(Executable runnable, String message) {
        ClientRequestException thrown = assertThrows(ClientRequestException.class, runnable);
        assertEquals(message, thrown.getMessage());
    }
}
