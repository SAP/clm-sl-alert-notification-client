package com.sap.cloud.alert.notification.client.model;

import io.pivotal.cfenv.core.CfCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlertNotificationServiceBindingTest {

    private static final String OAUTH_URL = "oauth_url";
    private static final String TEST_CERTIFICATE = "TEST_CERTIFICATE";
    private static final String TEST_PRIVATE_KEY = "TEST_PRIVATE_KEY";

    private Map<String, Object> credentials;
    private AlertNotificationServiceBinding classUnderTest;

    @BeforeEach
    public void setUp() {
        credentials = new HashMap<>();
        credentials.put("client_id", TEST_USERNAME);
        credentials.put("client_secret", TEST_PASSWORD);
        credentials.put("url", TEST_SERVICE_URI.toString());
        credentials.put("certificate", TEST_CERTIFICATE);
        credentials.put("key", TEST_PRIVATE_KEY);
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
    public void whenGetCertificateSecretIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getCertificate(), TEST_CERTIFICATE);
    }

    @Test
    public void whenGetPrivateKeySecretIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getPrivateKey(), TEST_PRIVATE_KEY);
    }

    @Test
    public void givenBindingOfTypeBasic_whenCreatingServiceBinding_thenCorrectServiceBindingIsReturned() {
        credentials.remove(OAUTH_URL);

        assertNull(new AlertNotificationServiceBinding(new CfCredentials(credentials)).getOauthUri());
    }

    @Test
    public void givenOAuthCredentials_whenCreatingServiceBinding_thenCorrectServiceBindingIsReturned() {
        AlertNotificationServiceBinding createdServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, URI.create(OAUTH_URL), TEST_USERNAME, TEST_PASSWORD);

        assertEquals(createdServiceBinding.getServiceUri(), TEST_SERVICE_URI);
        assertEquals(createdServiceBinding.getOauthUri(), URI.create(OAUTH_URL));
        assertEquals(createdServiceBinding.getClientId(), TEST_USERNAME);
        assertEquals(createdServiceBinding.getClientSecret(), TEST_PASSWORD);
        assertNull(createdServiceBinding.getCertificate());
        assertNull(createdServiceBinding.getPrivateKey());
    }

    @Test
    public void givenOAuthWithCertificateCredentials_whenCreatingServiceBinding_thenCorrectServiceBindingIsReturned() {
        AlertNotificationServiceBinding createdServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, URI.create(OAUTH_URL), TEST_USERNAME, TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        assertEquals(createdServiceBinding.getServiceUri(), TEST_SERVICE_URI);
        assertEquals(createdServiceBinding.getOauthUri(), URI.create(OAUTH_URL));
        assertEquals(createdServiceBinding.getClientId(), TEST_USERNAME);
        assertEquals(createdServiceBinding.getCertificate(), TEST_CERTIFICATE);
        assertEquals(createdServiceBinding.getPrivateKey(), TEST_PRIVATE_KEY);
        assertNull(createdServiceBinding.getClientSecret());
    }
}

