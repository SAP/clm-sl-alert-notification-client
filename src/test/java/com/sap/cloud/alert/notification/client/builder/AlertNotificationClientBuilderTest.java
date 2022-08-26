package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationClient;
import com.sap.cloud.alert.notification.client.internal.BasicAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.OAuthAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.SimpleRetryPolicy;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlertNotificationClientBuilderTest {

    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final URI TEST_OAUTH_SERVICE_URI = URI.create("https://nowhere.com");

    private static final Long TEST_INVALIDATION_TIME = 1L;

    private HttpClient testHttpClient;
    private IRetryPolicy testRetryPolicy;
    private ServiceRegion testServiceRegion;
    private AlertNotificationClientBuilder classUnderTest;
    private AlertNotificationServiceBinding alertNotificationServiceBinding;
    private DestinationServiceBinding destinationServiceBinding;

    @BeforeEach
    public void setUp() {
        testServiceRegion = ServiceRegion.EU10;
        testHttpClient = mock(HttpClient.class);
        testRetryPolicy = new SimpleRetryPolicy();
        classUnderTest = new AlertNotificationClientBuilder(testHttpClient);
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        destinationServiceBinding = new DestinationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_CLIENT_ID, TEST_CLIENT_SECRET).getValue(), createdClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, null, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_CLIENT_ID, TEST_CLIENT_SECRET).getValue(), createdClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatOAuthAuthenticationIsUsed_whenBuildIsCalled_thenCorrectAffectedClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion)
                .withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_OAUTH_SERVICE_URI).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatOAuthAuthenticationIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withBasicCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(BasicAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withOauthCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withCertificateCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createCertificateAuthenticationResponse())
                .thenReturn(createCertificateAuthenticationResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertNull(createdClient.getAuthorizationHeader());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withInvalidationTimeConfigured_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, TEST_INVALIDATION_TIME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(BasicAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
        assertCorrectInvalidationTime(createdClient, TEST_INVALIDATION_TIME);
    }

    @Test
    public void givenInvalidationTimeIsNegative_whenBuildFromDestinationServiceBindingIsCalled_thenExceptionIsThrown() {
        assertThrows(ClientRequestException.class, () -> classUnderTest.buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, -1L));
    }

    @Test
    public void givenThatNoHttpClientIsGiven_whenBuilderIsCreated_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClientBuilder(null);
        });
    }

    @Test
    public void givenThatNoServiceRegionIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest.withServiceRegion(null).withRetryPolicy(testRetryPolicy).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();
        });
    }

    @Test
    public void givenThatNullRetryPolicyIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest.withRetryPolicy(null).withServiceRegion(testServiceRegion).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();
        });
    }

    private void assertCorrectInvalidationTime(IAlertNotificationClient client, Long expectedInvalidationTime) {
        Long invalidationTime = extractSuperClassFieldValue(client, "invalidationTime");
        assertEquals(invalidationTime, expectedInvalidationTime);
    }
}
