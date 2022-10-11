package com.sap.cloud.alert.notification.client.model;

import io.pivotal.cfenv.core.CfCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DestinationServiceBindingTest {

    private static final String OAUTH_URL = "url";

    private DestinationServiceBinding classUnderTest;

    @BeforeEach
    public void setUp() {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("clientid", TEST_USERNAME);
        credentials.put("clientsecret", TEST_PASSWORD);
        credentials.put("uri", TEST_SERVICE_URI.toString());
        credentials.put(OAUTH_URL, TEST_OAUTH_SERVICE_URI.toString());

        classUnderTest = new DestinationServiceBinding(new CfCredentials(credentials));
    }

    @Test
    public void whenGetServiceUrlIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getServiceUri(), TEST_SERVICE_URI);
    }

    @Test
    public void whenGetOauthUrlIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getOauthUri(), TEST_OAUTH_SERVICE_URI);
    }

    @Test
    public void whenGetClientIdIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getClientId(), TEST_USERNAME);
    }

    @Test
    public void whenGetClientSecretIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getClientSecret(), TEST_PASSWORD);
    }
}
