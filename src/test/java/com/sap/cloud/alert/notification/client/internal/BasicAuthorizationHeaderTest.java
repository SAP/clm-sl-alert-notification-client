package com.sap.cloud.alert.notification.client.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicAuthorizationHeaderTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";

    private BasicAuthorizationHeader classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    public void givenThatUsernameIsNull_whenConstructingInstance_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new BasicAuthorizationHeader(null, TEST_PASSWORD);
        });
    }

    @Test
    public void givenThatPasswordIsNull_whenConstructingInstance_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new BasicAuthorizationHeader(TEST_USERNAME, null);
        });
    }

    @Test
    public void whenGetValueIsCalled_thenCorrectResultIsReturned() {
        assertEquals(format("Basic %s", encodeBase64(format("%s:%s", TEST_USERNAME, TEST_PASSWORD))), classUnderTest.getValue());
    }

    private static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(UTF_8)).trim();
    }
}
