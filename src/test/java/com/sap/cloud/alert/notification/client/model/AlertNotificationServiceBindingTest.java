package com.sap.cloud.alert.notification.client.model;

import io.pivotal.cfenv.core.CfCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AlertNotificationServiceBindingTest {

    private static final String OAUTH_URL = "oauth_url";

    private Map<String, Object> credentials;
    private AlertNotificationServiceBinding classUnderTest;

    @BeforeEach
    public void setUp() {
        credentials = new HashMap<>();
        credentials.put("client_id", TEST_USERNAME);
        credentials.put("client_secret", TEST_PASSWORD);
        credentials.put("url", TEST_SERVICE_URI.toString());
        credentials.put(OAUTH_URL, TEST_OAUTH_SERVICE_URI.toString());

        classUnderTest = new AlertNotificationServiceBinding(new CfCredentials(credentials));
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

    @Test
    public void givenBindingOfTypeBasic_whenCreatingServiceBinding_thenCorrectServiceBindingIsReturned() {
        credentials.remove(OAUTH_URL);

        assertNull(new AlertNotificationServiceBinding(new CfCredentials(credentials)).getOauthUri());
    }

}

