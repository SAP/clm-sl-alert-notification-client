package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationConfigurationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;

public final class AlertNotificationConfigurationClient implements IAlertNotificationConfigurationClient {

    private final HttpClient httpClient;
    private final IRetryPolicy retryPolicy;
    private final ServiceRegion serviceRegion;
    private final IAuthorizationHeader authorizationHeader;
    private final URI actionBaseUri;
    private final URI conditionBaseUri;
    private final URI subscriptionBaseUri;

    public AlertNotificationConfigurationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            IAuthorizationHeader authorizationHeader
    ) {
        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
        this.authorizationHeader = authorizationHeader;
        this.actionBaseUri = buildActionsUri(serviceRegion, emptyMap());
        this.conditionBaseUri = buildConditionsUri(serviceRegion, emptyMap());
        this.subscriptionBaseUri = buildSubscriptionsUri(serviceRegion, emptyMap());
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public IRetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public ServiceRegion getServiceRegion() {
        return serviceRegion;
    }

    public IAuthorizationHeader getAuthorizationHeader() {
        return authorizationHeader;
    }

    @Override
    public ConfigurationResponse<Condition> getConditions(Map<ConfigurationQueryParameter, String> queryParameters) {
        URI getConditionsUri = buildConditionsUri(serviceRegion, queryParameters);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getConditionsUri)), CONDITION_CONFIGURATION_TYPE);
    }

    @Override
    public Condition createCondition(Condition condition) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(conditionBaseUri, toJsonString(condition))), CONDITION_TYPE);
    }

    @Override
    public Condition getCondition(String conditionName) {
        URI getConditionUri = buildConditionUri(serviceRegion, conditionName);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getConditionUri)), CONDITION_TYPE);
    }

    @Override
    public Condition updateCondition(Condition condition) {
        URI updateConditionUri = buildConditionUri(serviceRegion, condition.getName());
        return fromJsonString(executeWithRetry(() -> executeHttpPut(updateConditionUri, toJsonString(condition))), CONDITION_TYPE);
    }

    @Override
    public void deleteCondition(String conditionName) {
        URI deleteConditionUri = buildConditionUri(serviceRegion, conditionName);
        executeWithRetry(() -> executeHttpDelete(deleteConditionUri));
    }

    @Override
    public ConfigurationResponse<Action> getActions(Map<ConfigurationQueryParameter, String> queryParameters) {
        URI getActionsUri = buildActionsUri(serviceRegion, queryParameters);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getActionsUri)), ACTION_CONFIGURATION_TYPE);
    }

    @Override
    public Action createAction(Action action) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(actionBaseUri, toJsonString(action))), ACTION_TYPE);
    }

    @Override
    public Action getAction(String actionName) {
        URI getActionUri = buildActionUri(serviceRegion, actionName);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getActionUri)), ACTION_TYPE);
    }

    @Override
    public Action updateAction(Action action) {
        URI updateActionUri = buildActionUri(serviceRegion, action.getName());
        return fromJsonString(executeWithRetry(() -> executeHttpPut(updateActionUri, toJsonString(action))), ACTION_TYPE);
    }

    @Override
    public void deleteAction(String actionName) {
        URI deleteActionUri = buildActionUri(serviceRegion, actionName);
        executeWithRetry(() -> executeHttpDelete(deleteActionUri));
    }

    @Override
    public ConfigurationResponse<Subscription> getSubscriptions(Map<ConfigurationQueryParameter, String> queryParameters) {
        URI getSubscriptionsUri = buildSubscriptionsUri(serviceRegion, queryParameters);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getSubscriptionsUri)), SUBSCRIPTION_CONFIGURATION_TYPE);
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(subscriptionBaseUri, toJsonString(subscription))), SUBSCRIPTION_TYPE);
    }

    @Override
    public Subscription getSubscription(String subscriptionName) {
        URI getSubscriptionUri = buildSubscriptionUri(serviceRegion, subscriptionName);
        return fromJsonString(executeWithRetry(() -> executeHttpGet(getSubscriptionUri)), SUBSCRIPTION_TYPE);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        URI updateSubscriptionUri = buildSubscriptionUri(serviceRegion, subscription.getName());
        return fromJsonString(executeWithRetry(() -> executeHttpPut(updateSubscriptionUri, toJsonString(subscription))), SUBSCRIPTION_TYPE);
    }

    @Override
    public void deleteSubscription(String subscriptionName) {
        URI deleteSubscriptionUri = buildSubscriptionUri(serviceRegion, subscriptionName);
        executeWithRetry(() -> executeHttpDelete(deleteSubscriptionUri));
    }

    @Override
    public Configuration importConfiguration(Configuration newConfiguration) {
        URI configurationManagementBaseUri = buildConfigurationManagementUri(serviceRegion);
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpPost(configurationManagementBaseUri, toJsonString(newConfiguration))), CONFIGURATION_TYPE);
    }

    @Override
    public Configuration exportConfiguration() {
        URI configurationManagementBaseUri = buildConfigurationManagementUri(serviceRegion);
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(configurationManagementBaseUri)), CONFIGURATION_TYPE);
    }

    private String executeWithRetry(Supplier<String> supplier) {
       return retryPolicy.executeWithRetry(supplier);
    }

    private String executeHttpPost(URI serviceUri, String payload) {
        return executeRequest(createPostRequest(serviceUri, payload), SC_CREATED);
    }

    private String executeHttpGet(URI serviceUri) {
        return executeRequest(createGetRequest(serviceUri), SC_OK);
    }

    private String executeHttpPut(URI serviceUri, String payload) {
        return executeRequest(createPutRequest(serviceUri, payload), SC_OK);
    }

    private String executeHttpDelete(URI serviceUri) {
        return executeRequest(createDeleteRequest(serviceUri), SC_NO_CONTENT);
    }

    private String executeRequest(HttpUriRequest httpRequest, int expectedStatus) {
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpRequest);

            assertHttpStatus(response, expectedStatus);

            return isNull(response.getEntity()) ? EMPTY : EntityUtils.toString(response.getEntity(), UTF_8.name());
        } catch (IOException exception) {
            throw new ClientRequestException(exception);
        } finally {
            consumeQuietly(response);
        }
    }

    private HttpGet createGetRequest(URI serviceUri) {
        HttpGet request = new HttpGet(serviceUri);

        request.setHeader(ACCEPT, APPLICATION_JSON);
        setAuthorizationHeader(request);

        return request;
    }

    private HttpPost createPostRequest(URI serviceUri, String payload) {
        HttpPost request = new HttpPost(serviceUri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setEntity(toStringEntity(payload));
        setAuthorizationHeader(request);

        return request;
    }

    private HttpPut createPutRequest(URI serviceUri, String payload) {
        HttpPut request = new HttpPut(serviceUri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setEntity(toStringEntity(payload));
        setAuthorizationHeader(request);

        return request;
    }

    private HttpDelete createDeleteRequest(URI serviceUri) {
        HttpDelete request = new HttpDelete(serviceUri);

        setAuthorizationHeader(request);

        return request;
    }

    private void setAuthorizationHeader(HttpRequest request) {
        if (authorizationHeader != null) {
            request.setHeader(AUTHORIZATION, authorizationHeader.getValue());
        }
    }

    private StringEntity toStringEntity(String payload) {
        return new StringEntity(payload, UTF_8.name());
    }

}