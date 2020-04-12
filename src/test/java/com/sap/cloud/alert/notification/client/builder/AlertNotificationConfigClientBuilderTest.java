package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.internal.AlertNotificationConfigClient;
import com.sap.cloud.alert.notification.client.internal.BasicAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.OAuthAuthorizationHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class AlertNotificationConfigClientBuilderTest {

    private AlertNotificationConfigClientBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new AlertNotificationConfigClientBuilder();
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationConfigClient alertNotificationConfigurationClient = (AlertNotificationConfigClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .withAuthentication(TEST_USERNAME, TEST_PASSWORD) //
                .build();

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(TEST_RETRY_POLICY.getMaxRetries(), alertNotificationConfigurationClient.getRetryPolicy().getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD).getValue(), alertNotificationConfigurationClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatOAuthIsUsedForAuthentication_whenBuildIsCalled_thenCorrectClientIsBuilt() {
        AlertNotificationConfigClient alertNotificationConfigurationClient = (AlertNotificationConfigClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .withAuthentication(TEST_USERNAME, TEST_PASSWORD, TEST_OAUTH_SERVICE_URI) //
                .build();

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(TEST_RETRY_POLICY.getMaxRetries(), alertNotificationConfigurationClient.getRetryPolicy().getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, alertNotificationConfigurationClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatNoCredentialsAreGiven_whenBuildIsCalled_thenCorrectClientIsBuilt() {
        AlertNotificationConfigClient alertNotificationConfigurationClient = (AlertNotificationConfigClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .build();

        assertNull(alertNotificationConfigurationClient.getAuthorizationHeader());
        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(TEST_RETRY_POLICY.getMaxRetries(), alertNotificationConfigurationClient.getRetryPolicy().getMaxRetries());
    }

    @Test
    public void givenThatNoHttpClientIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .build()
        );
    }

    @Test
    public void givenThatNoServiceRegionIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .build()
        );
    }

    @Test
    public void givenThatNullHttpClientIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withHttpClient(null) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .build()
        );
    }

    @Test
    public void givenThatNullRetryPolicyIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(null) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .build()
        );
    }

    @Test
    public void givenThatNullServiceRegionIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(null)
                .build()
        );
    }

}
