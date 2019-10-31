package com.sap.cloud.alert.notification.client.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientRequestExceptionTest {

    private static final IOException TEST_CAUSE = new IOException();

    @Test
    public void whenCreatingInstance_thenCorrectPropertiesAreSet() {
        ClientRequestException exception = new ClientRequestException(TEST_CAUSE);

        assertEquals(TEST_CAUSE, exception.getCause());
    }
}
