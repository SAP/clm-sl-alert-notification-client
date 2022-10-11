package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.internal.*;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AlertNotificationConfigurationClientBuilderTest {

    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";

    private static final String TEST_USERNAME = "username";
    private static final String TEST_PASSWORD = "password";

    private DestinationServiceBinding destinationServiceBinding;
    private AlertNotificationConfigurationClientBuilder classUnderTest;
    private AlertNotificationServiceBinding alertNotificationServiceBinding;

    private static final Long TEST_INVALIDATION_TIME = 1L;

    @BeforeEach
    public void setUp() {
        classUnderTest = new AlertNotificationConfigurationClientBuilder();

        destinationServiceBinding = new DestinationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .withAuthentication(TEST_USERNAME, TEST_PASSWORD) //
                .build();

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(), ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD).getValue(), alertNotificationConfigurationClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, null, TEST_USERNAME, TEST_PASSWORD);
        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(), ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD).getValue(), alertNotificationConfigurationClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withBasicCredentials_thenCorrectClientIsCreated() throws Exception {
        when(TEST_HTTP_CLIENT.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(alertNotificationConfigurationClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(), ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD).getValue(), alertNotificationConfigurationClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withOAuthCredentials_thenCorrectClientIsCreated() throws Exception {
        when(TEST_HTTP_CLIENT.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse());

        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(alertNotificationConfigurationClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(), ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, alertNotificationConfigurationClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withCertificateCredentials_thenCorrectClientIsCreated() throws Exception {
        when(TEST_HTTP_CLIENT.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createCertificateAuthenticationResponse())
                .thenReturn(createCertificateAuthenticationResponse());

        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(alertNotificationConfigurationClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(), ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertNull(alertNotificationConfigurationClient.getAuthorizationHeader());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withInvalidationTimeConfigured_thenCorrectClientIsCreated() throws Exception {
        when(TEST_HTTP_CLIENT.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, TEST_INVALIDATION_TIME);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(alertNotificationConfigurationClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertCorrectInvalidationTime(alertNotificationConfigurationClient, TEST_INVALIDATION_TIME);
    }

    @Test
    public void givenInvalidationTimeIsNegative_whenBuildFromDestinationServiceBindingIsCalled_thenExceptionIsThrown() {
        assertThrows(ClientRequestException.class, () -> classUnderTest.buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, -1L));
    }

    @Test
    public void givenThatOAuthIsUsedForAuthentication_whenBuildIsCalled_thenCorrectClientIsBuilt() {
        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .withAuthentication(TEST_USERNAME, TEST_PASSWORD, TEST_OAUTH_SERVICE_URI) //
                .build();

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(),  ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, alertNotificationConfigurationClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatOAuthIsUsedForAuthentication_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsBuilt() {
        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(),  ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, alertNotificationConfigurationClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatNoCredentialsAreGiven_whenBuildIsCalled_thenCorrectClientIsBuilt() {
        AlertNotificationConfigurationClient alertNotificationConfigurationClient = (AlertNotificationConfigurationClient) classUnderTest //
                .withHttpClient(TEST_HTTP_CLIENT) //
                .withRetryPolicy(TEST_RETRY_POLICY) //
                .withServiceRegion(TEST_SERVICE_REGION)
                .build();

        assertNull(alertNotificationConfigurationClient.getAuthorizationHeader());
        assertEquals(TEST_HTTP_CLIENT, alertNotificationConfigurationClient.getHttpClient());
        assertEquals(TEST_SERVICE_REGION, alertNotificationConfigurationClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries(),  ((SimpleRetryPolicy) alertNotificationConfigurationClient.getRetryPolicy()).getMaxRetries());
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

    private void assertCorrectInvalidationTime(AlertNotificationConfigurationClient client, Long expectedInvalidationTime) {
        Long invalidationTime = extractSuperClassFieldValue(client, "invalidationTime");
        assertEquals(invalidationTime, expectedInvalidationTime);
    }
}
