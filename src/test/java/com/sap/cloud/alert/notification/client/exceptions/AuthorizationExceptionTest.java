package com.sap.cloud.alert.notification.client.exceptions;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorizationExceptionTest {

    private static final String TEST_MESSAGE = "TEST_MESSAGE";
    private static final String TEST_HEADER = "TEST_HEADER";
    private static final int TEST_STATUS = HttpStatus.SC_FORBIDDEN;

    @Test
    public void whenCreatingInstance_thenCorrectPropertiesAreSet() {
        AuthorizationException exception = new AuthorizationException(TEST_MESSAGE, TEST_STATUS, TEST_HEADER);

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_STATUS, exception.getStatusCode());
        assertEquals(TEST_HEADER, exception.getxVcapRequestId());
    }
}
