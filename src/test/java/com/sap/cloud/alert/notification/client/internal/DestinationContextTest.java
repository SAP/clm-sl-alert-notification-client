package com.sap.cloud.alert.notification.client.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DestinationContextTest {

    private static final String TEST_SERVICE_URI = "TEST_SERVICE_URI";
    private static final boolean IS_CERTIFICATE_AUTHENTICATION = TRUE;

    private DestinationContext classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new DestinationContext(TEST_SERVICE_URI, IS_CERTIFICATE_AUTHENTICATION);
    }

    @Test
    public void whenGetServiceUriIsCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.getServiceUri(), TEST_SERVICE_URI);
    }

    @Test
    public void whenIsCertificateAuthenticationIsCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.isCertificateAuthentication(), TRUE);
    }
}
