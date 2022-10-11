package com.sap.cloud.alert.notification.client.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.builder.CustomerResourceEventBuilder;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.*;
import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertNotificationClientTest {

    private static final String TEST_PROTOCOL_NAME = "HTTP";
    private static final int TEST_PROTOCOL_MAJOR_VERSION = 3;
    private static final int TEST_PROTOCOL_MINOR_VERSION = 0;
    private static final String TEST_EVENT_ID = "TEST_EVENT_ID";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_REASON_PHRASE = "Test Reason";
    private static final ServiceRegion TEST_SERVICE_REGION = ServiceRegion.EU1;
    private static final IRetryPolicy TEST_RETRY_POLICY = new SimpleRetryPolicy(3, Duration.ofMillis(100));
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final ProtocolVersion TEST_PROTOCOL_VERSION = new ProtocolVersion(TEST_PROTOCOL_NAME, TEST_PROTOCOL_MAJOR_VERSION, TEST_PROTOCOL_MINOR_VERSION);
    private static final KeyStoreDetails TEST_KEYSTORE_DETAILS_PEM = new KeyStoreDetails(KeyStoreType.PEM, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_PEM);
    private static final KeyStoreDetails TEST_KEYSTORE_DETAILS_P12 = new KeyStoreDetails(KeyStoreType.PKCS12, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_P12);

    private static final CustomerResourceEvent TEST_CUSTOMER_RESOURCE_EVENT = new CustomerResourceEventBuilder().withBody("TEST_BODY")
            .withType("TEST_TYPE")
            .withSubject("TEST_SUBJECT")
            .withSeverity(EventSeverity.INFO)
            .withCategory(EventCategory.NOTIFICATION)
            .withAffectedResource(new AffectedCustomerResource("TEST_NAME", "TEST_TYPE", "TEST_INSTANCE", Collections.emptyMap()))
            .build();

    private HttpClientFactory mockedHttpClientFactory;
    private DestinationCredentialsProvider mockedDestinationCredentialsProvider;
    private HttpClient mockedHttpClient;
    private AlertNotificationClient classUnderTest;
    private IAuthorizationHeader authorizationHeader;

    @BeforeEach
    public void setUp() {
        mockedHttpClientFactory = Mockito.mock(HttpClientFactory.class);
        mockedDestinationCredentialsProvider = Mockito.mock(DestinationCredentialsProvider.class);
        mockedHttpClient = Mockito.mock(HttpClient.class);
        authorizationHeader = new BasicAuthorizationHeader(TEST_USERNAME, TEST_PASSWORD);
        classUnderTest = new AlertNotificationClient(mockedHttpClient, TEST_RETRY_POLICY, TEST_SERVICE_REGION, authorizationHeader);
    }

    @Test
    public void givenHttpClientIsNull_whenConstructingAlertNotificationClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClient(null, TEST_RETRY_POLICY, TEST_SERVICE_REGION, authorizationHeader);
        });
    }

    @Test
    public void givenRetryPolicyIsNull_whenConstructingAlertNotificationClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClient(mockedHttpClient, null, TEST_SERVICE_REGION, authorizationHeader);
        });
    }

    @Test
    public void givenServiceRegionIsNull_whenConstructingAlertNotificationClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClient(mockedHttpClient, TEST_RETRY_POLICY, null, authorizationHeader);
        });
    }

    @Test
    public void givenAuthorizationHeaderIsNull_whenConstructingAlertNotificationClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClient(mockedHttpClient, TEST_RETRY_POLICY, TEST_SERVICE_REGION, null);
        });
    }

    @Test
    public void whenGetHttpClientIsCalled_thenCorrectResultIsReturned() {
        assertEquals(mockedHttpClient, classUnderTest.getHttpClient());
    }

    @Test
    public void whenGetServiceRegionIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_SERVICE_REGION, classUnderTest.getServiceRegion());
    }

    @Test
    public void whenGetAuthorizationHeaderIsCalled_thenCorrectResultIsReturned() {
        assertEquals(authorizationHeader, classUnderTest.getAuthorizationHeader());
    }

    @Test
    public void whenSendEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpPost> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

        doReturn(createMockedSendEventResponse(TEST_CUSTOMER_RESOURCE_EVENT)).when(mockedHttpClient).execute(any(HttpPost.class));

        classUnderTest.sendEvent(TEST_CUSTOMER_RESOURCE_EVENT);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpPost sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("POST", sentRequest.getMethod());
        assertEquals(buildProducerURI(TEST_SERVICE_REGION).toString(), sentRequest.getURI().toString());
        assertEquals(authorizationHeader.getValue(), sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(ContentType.APPLICATION_JSON.toString(), sentRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
        assertEquals(JSON_OBJECT_MAPPER.writeValueAsString(TEST_CUSTOMER_RESOURCE_EVENT), IOUtils.toString(sentRequest.getEntity().getContent(), StandardCharsets.UTF_8));
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication1_whenSendEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpPost> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);
        doReturn(createMockedSendEventResponse(TEST_CUSTOMER_RESOURCE_EVENT)).when(mockedHttpClient).execute(any(HttpPost.class));

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );
        Thread.sleep(2000);

        classUnderTest.sendEvent(TEST_CUSTOMER_RESOURCE_EVENT);

        verify(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpPost sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("POST", sentRequest.getMethod());
        assertEquals(buildProducerURI(TEST_SERVICE_REGION).toString(), sentRequest.getURI().toString());
        assertEquals(ContentType.APPLICATION_JSON.toString(), sentRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
        assertEquals(JSON_OBJECT_MAPPER.writeValueAsString(TEST_CUSTOMER_RESOURCE_EVENT), IOUtils.toString(sentRequest.getEntity().getContent(), StandardCharsets.UTF_8));
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication2_whenSendEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpPost> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);
        doReturn(createMockedSendEventResponse(TEST_CUSTOMER_RESOURCE_EVENT)).when(mockedHttpClient).execute(any(HttpPost.class));

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.sendEvent(TEST_CUSTOMER_RESOURCE_EVENT);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpPost sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("POST", sentRequest.getMethod());
        assertEquals(buildProducerURI(TEST_SERVICE_REGION).toString(), sentRequest.getURI().toString());
        assertEquals(ContentType.APPLICATION_JSON.toString(), sentRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
        assertEquals(JSON_OBJECT_MAPPER.writeValueAsString(TEST_CUSTOMER_RESOURCE_EVENT), IOUtils.toString(sentRequest.getEntity().getContent(), StandardCharsets.UTF_8));
    }

    @Test
    public void givenThatSendingRequestFails_whenSendEventIsCalled_thenRequestIsRetried() throws Exception {
        doReturn(createFailedResponse()).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.sendEvent(TEST_CUSTOMER_RESOURCE_EVENT));

        verify(mockedHttpClient, times(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries() + 1)).execute(any(HttpPost.class));
    }

    @Test
    public void whenGetMatchedEventsIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));

        classUnderTest.getMatchedEvents(testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildMatchedEventsURI(TEST_SERVICE_REGION, testQueryFilters).toString(), sentRequest.getURI().toString());
        assertEquals(authorizationHeader.getValue(), sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }

    @Test
    public void givenThatSendingRequestFails_whenGetMatchedEventsIsCalled_thenRequestIsRetried() throws Exception {
        doReturn(createFailedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        assertThrows(ServerResponseException.class, () -> classUnderTest.getMatchedEvents(testQueryFilters));

        verify(mockedHttpClient, times(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries() + 1)).execute(any(HttpGet.class));
    }

    @Test
    public void whenGetMatchedEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));

        classUnderTest.getMatchedEvent(TEST_EVENT_ID, testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(authorizationHeader.getValue(), sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(buildMatchedEventsURI(TEST_SERVICE_REGION, TEST_EVENT_ID, testQueryFilters).toString(), sentRequest.getURI().toString());
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication1_whenGetMatchedEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.getMatchedEvent(TEST_EVENT_ID, testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildMatchedEventsURI(TEST_SERVICE_REGION, TEST_EVENT_ID, testQueryFilters).toString(), sentRequest.getURI().toString());
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication2_whenGetMatchedEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.getMatchedEvent(TEST_EVENT_ID, testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildMatchedEventsURI(TEST_SERVICE_REGION, TEST_EVENT_ID, testQueryFilters).toString(), sentRequest.getURI().toString());
    }

    @Test
    public void givenThatSendingRequestFails_whenGetMatchedEventIsCalled_thenRequestIsRetried() throws Exception {
        doReturn(createFailedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        assertThrows(ServerResponseException.class, () -> classUnderTest.getMatchedEvent(TEST_EVENT_ID, testQueryFilters));

        verify(mockedHttpClient, times(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries() + 1)).execute(any(HttpGet.class));
    }

    @Test
    public void whenGetUndeliveredEventsIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));

        classUnderTest.getUndeliveredEvents(testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildUndeliveredEventsURI(TEST_SERVICE_REGION, testQueryFilters).toString(), sentRequest.getURI().toString());
        assertEquals(authorizationHeader.getValue(), sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication1_whenGetUndeliveredEventsIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.getUndeliveredEvents(testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildUndeliveredEventsURI(TEST_SERVICE_REGION, testQueryFilters).toString(), sentRequest.getURI().toString());
        assertNull(sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication2_whenGetUndeliveredEventsIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationClient(
                mockedHttpClient,
                TEST_RETRY_POLICY,
                TEST_SERVICE_REGION,
                null,
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.getUndeliveredEvents(testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(buildUndeliveredEventsURI(TEST_SERVICE_REGION, testQueryFilters).toString(), sentRequest.getURI().toString());
        assertNull(sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void givenThatSendingRequestFails_whenGetUndeliveredEventsIsCalled_thenRequestIsRetried() throws Exception {
        doReturn(createFailedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        assertThrows(ServerResponseException.class, () -> classUnderTest.getUndeliveredEvents(testQueryFilters));

        verify(mockedHttpClient, times(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries() + 1)).execute(any(HttpGet.class));
    }

    @Test
    public void whenGetUndeliveredEventIsCalled_thenCorrectRequestIsSent() throws Exception {
        ArgumentCaptor<HttpGet> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        doReturn(createMockedPagedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));

        classUnderTest.getUndeliveredEvent(TEST_EVENT_ID, testQueryFilters);

        verify(mockedHttpClient).execute(httpRequestArgumentCaptor.capture());

        HttpGet sentRequest = httpRequestArgumentCaptor.getValue();

        assertEquals("GET", sentRequest.getMethod());
        assertEquals(authorizationHeader.getValue(), sentRequest.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(buildUndeliveredEventsURI(TEST_SERVICE_REGION, TEST_EVENT_ID, testQueryFilters).toString(), sentRequest.getURI().toString());
    }

    @Test
    public void givenThatSendingRequestFails_whenGetUndeliveredEventIsCalled_thenRequestIsRetried() throws Exception {
        doReturn(createFailedResponse()).when(mockedHttpClient).execute(any(HttpGet.class));
        Map<QueryParameter, String> testQueryFilters = Collections.singletonMap(QueryParameter.EVENT_TYPE, "TEST_EVENT_TYPE");

        assertThrows(ServerResponseException.class, () -> classUnderTest.getUndeliveredEvent(TEST_EVENT_ID, testQueryFilters));

        verify(mockedHttpClient, times(((SimpleRetryPolicy) TEST_RETRY_POLICY).getMaxRetries() + 1)).execute(any(HttpGet.class));
    }

    private HttpResponse createFailedResponse() {
        return new BasicHttpResponse(new BasicStatusLine(TEST_PROTOCOL_VERSION, SC_INTERNAL_SERVER_ERROR, TEST_REASON_PHRASE));
    }

    private HttpResponse createMockedSendEventResponse(CustomerResourceEvent event) throws Exception {
        StringEntity responseEntity = new StringEntity(JSON_OBJECT_MAPPER.writeValueAsString(event));
        StatusLine statusline = new BasicStatusLine(TEST_PROTOCOL_VERSION, SC_ACCEPTED, TEST_REASON_PHRASE);
        HttpResponse httpResponse = new BasicHttpResponse(statusline);

        httpResponse.setEntity(responseEntity);

        return httpResponse;
    }

    private HttpResponse createMockedPagedResponse() throws Exception {
        PageMetadata pageMetadata = new PageMetadata(0, 0, 0L, 0L);
        PagedResponse pagedResponse = new PagedResponse(pageMetadata, Collections.emptyList());
        StringEntity responseEntity = new StringEntity(JSON_OBJECT_MAPPER.writeValueAsString(pagedResponse));
        StatusLine statusline = new BasicStatusLine(TEST_PROTOCOL_VERSION, SC_OK, TEST_REASON_PHRASE);
        HttpResponse httpResponse = new BasicHttpResponse(statusline);

        httpResponse.setEntity(responseEntity);

        return httpResponse;
    }
}
