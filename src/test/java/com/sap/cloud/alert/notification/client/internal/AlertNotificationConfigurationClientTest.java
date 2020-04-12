package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import net.jodah.failsafe.RetryPolicy;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.APPLICATION_JSON;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.EMPTY;
import static com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter.*;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertNotificationConfigurationClientTest {

    private static final String TEST_PAGE_QUERY = "3";
    private static final String TEST_PAGE_SIZE_QUERY = "50";
    private static final String TEST_STATUS_LINE = "TEST_STATUS_LINE";
    private static final RetryPolicy TEST_RETRY_POLICY = new RetryPolicy().withMaxRetries(0);
    private static final String TEST_AUTHORIZATION_HEADER = "Basic TEST_AUTHORIZATION_HEADER";
    private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);

    private static final Action TEST_ACTION = new Action(TEST_TYPE, TEST_NAME, TEST_STATE, TEST_DESCRIPTION, TEST_LABELS, TEST_FALLBACK_TIME,
            TEST_FALLBACK_ACTION, TEST_PROPERTIES);
    private static final Subscription TEST_SUBSCRIPTION = new Subscription(TEST_NAME, TEST_STATE, TEST_DESCRIPTION, TEST_LABELS, TEST_ACTIONS,
            TEST_CONDITIONS);
    private static final ConfigurationErrorResponse CONFIGURATION_ERROR_RESPONSE = new ConfigurationErrorResponse(SC_INTERNAL_SERVER_ERROR,
            EMPTY);
    private static final ConfigurationResponse<Action> ACTION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(singletonList(TEST_ACTION),
            TEST_CONFIGURATION_PAGING_METADATA);
    private static final ConfigurationResponse<Condition> CONDITION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(
            singletonList(TEST_CONDITION), TEST_CONFIGURATION_PAGING_METADATA);
    private static final ConfigurationResponse<Subscription> SUBSCRIPTION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(
            singletonList(TEST_SUBSCRIPTION), TEST_CONFIGURATION_PAGING_METADATA);

    private static final String ACTION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/action/%s";
    private static final String ACTIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/action", TEST_SERVICE_REGION.getPlatform().getKey());
    private static final String CONDITION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/condition/%s";
    private static final String CONDITIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/condition", TEST_SERVICE_REGION.getPlatform().getKey());
    private static final String SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/subscription/%s";
    private static final String SUBSCRIPTIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/subscription", TEST_SERVICE_REGION.getPlatform().getKey());

    private HttpClient mockedHttpClient;
    private AlertNotificationConfigClient classUnderTest;
    private ArgumentCaptor<HttpUriRequest> requestCaptor;
    private IAuthorizationHeader mockedAuthorizationHeader;
    private Map<ConfigurationQueryParameter, String> requestParameters;

    @BeforeEach
    public void setUp() {
        mockedHttpClient = mock(HttpClient.class);
        mockedAuthorizationHeader = mock(IAuthorizationHeader.class);
        classUnderTest = new AlertNotificationConfigClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                mockedAuthorizationHeader //
        );

        requestParameters = new HashMap<>();
        requestParameters.put(PAGE_SIZE, TEST_PAGE_SIZE_QUERY);
        requestParameters.put(PAGE, TEST_PAGE_QUERY);

        doReturn(TEST_AUTHORIZATION_HEADER).when(mockedAuthorizationHeader).getValue();
    }

    @Test
    public void whenGetConditionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, CONDITION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Condition> conditions = classUnderTest.getConditions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(CONDITION_CONFIGURATION_RESPONSE, conditions);
    }

    @Test
    public void givenThatRequestFails_whenGetConditionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getConditions(requestParameters));
    }

    @Test
    public void whenCreatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Condition condition = classUnderTest.createCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenCreatingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createCondition(TEST_CONDITION));
    }

    @Test
    public void whenGetConditionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Condition condition = classUnderTest.getCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenGetConditionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getCondition(TEST_CONDITION.getName()));
    }

    @Test
    public void whenUpdatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Condition condition = classUnderTest.updateCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName()),
                        TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateCondition(TEST_CONDITION));
    }

    @Test
    public void whenDeletingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteCondition(TEST_CONDITION.getName()));
    }

    @Test
    public void whenGetActionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, ACTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Action> actions = classUnderTest.getActions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(ACTION_CONFIGURATION_RESPONSE, actions);

    }

    @Test
    public void givenThatRequestFails_whenGetActionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getActions(requestParameters));
    }

    @Test
    public void whenCreatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Action action = classUnderTest.createAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH), TEST_ACTION) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenCreatingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createAction(TEST_ACTION));
    }

    @Test
    public void whenGetActionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Action action = classUnderTest.getAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenGetActionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getAction(TEST_ACTION.getName()));
    }

    @Test
    public void whenUpdatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Action action = classUnderTest.updateAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName()), TEST_ACTION)
                //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateAction(TEST_ACTION));
    }

    @Test
    public void whenDeletingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteAction(TEST_ACTION.getName()));
    }

    @Test
    public void whenGetSubscriptionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, SUBSCRIPTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Subscription> subscriptions = classUnderTest.getSubscriptions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(SUBSCRIPTION_CONFIGURATION_RESPONSE, subscriptions);

    }

    @Test
    public void givenThatRequestFails_whenGetSubscriptionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getSubscriptions(requestParameters));
    }

    @Test
    public void whenCreatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Subscription subscription = classUnderTest.createSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenCreatingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createSubscription(TEST_SUBSCRIPTION));
    }

    @Test
    public void whenGetSubscriptionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Subscription subscription = classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName())) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenGetSubscriptionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName()));
    }

    @Test
    public void whenUpdatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Subscription subscription = classUnderTest.updateSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()),
                        TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateSubscription(TEST_SUBSCRIPTION));
    }

    @Test
    public void whenDeletingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()))
                //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, CONFIGURATION_ERROR_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName()));
    }

    private static void assertCorrectRequest(HttpGet found, HttpGet expected) {
        assertCommonHttpRequestProperties(found, expected);
    }

    private static void assertCorrectRequest(HttpPost found, HttpPost expected) throws IOException {
        assertCommonHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectRequest(HttpPut found, HttpPut expected) throws IOException {
        assertCommonHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectRequest(HttpDelete found, HttpDelete expected) {
        assertCommonHttpRequestProperties(found, expected);
    }

    private static void assertCommonHttpRequestProperties(HttpUriRequest found, HttpUriRequest expected) {
        assertEquals(expected.getURI(), found.getURI());
        assertEquals(expected.getMethod(), found.getMethod());
        assertEquals(TEST_AUTHORIZATION_HEADER, found.getFirstHeader(AUTHORIZATION).getValue());
    }

    private static URI toRequestURI(URI uri, String path) throws URISyntaxException {
        return buildUri(uri, path, null);
    }

    private static URI toRequestURI(URI uri, String path, Map<ConfigurationQueryParameter, String> parameters) throws URISyntaxException {
        return buildUri(uri, path, toConfigurationQueryParameters(parameters));
    }

    private static URI toRequestURI(URI uri, String path, String entityName) throws URISyntaxException {
        return buildUri(uri, format(path, TEST_SERVICE_REGION.getPlatform().getKey(), entityName), null);
    }

    private static HttpResponse buildResponse(int statusCode, Object responseEntity) {
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, statusCode, TEST_STATUS_LINE));

        response.setEntity(new StringEntity(toJsonString(responseEntity), UTF_8.name()));

        return response;
    }

    private static HttpGet buildExpectedGetRequest(URI uri) {
        HttpGet request = new HttpGet(uri);

        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);

        return request;
    }

    private static HttpPost buildExpectedPostRequest(URI uri, Object payload) {
        HttpPost request = new HttpPost(uri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);
        request.setEntity(new StringEntity(toJsonString(payload), UTF_8.name()));

        return request;
    }

    private static HttpPut buildExpectedPutRequest(URI uri, Object payload) {
        HttpPut request = new HttpPut(uri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);
        request.setEntity(new StringEntity(toJsonString(payload), UTF_8.name()));

        return request;
    }

    private static HttpDelete buildExpectedDeleteRequest(URI uri) {
        HttpDelete request = new HttpDelete(uri);

        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);

        return request;
    }

    private static List<NameValuePair> toConfigurationQueryParameters(Map<ConfigurationQueryParameter, String> queryFilters) {
        return queryFilters.entrySet().stream().filter(entry -> QUERY_VIABLE_PARAMETERS.contains(entry.getKey()))
                .map(queryFilter -> new BasicNameValuePair(queryFilter.getKey().getKey(), queryFilter.getValue())).collect(toList());
    }

    private static URI buildUri(URI uri, String path, List<NameValuePair> parameters) throws URISyntaxException {
     URIBuilder builder = new URIBuilder()
                .setScheme(uri.getScheme())
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setPath(path);

     return isNull(parameters)
             ? builder.build()
             : builder.setParameters(parameters).build();
    }
}
