package com.sap.cloud.alert.notification.client.exceptions;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerResponseExceptionTest {

    private static final String TEST_MESSAGE = "TEST_MESSAGE";
    private static final int TEST_STATUS = HttpStatus.SC_INTERNAL_SERVER_ERROR;

    @Test
    public void whenCreatingInstance_thenCorrectPropertiesAreSet() {
        ServerResponseException exception = new ServerResponseException(TEST_MESSAGE, TEST_STATUS);

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_STATUS, exception.getStatusCode());
    }
}
