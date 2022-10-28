package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationConfigurationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import org.apache.http.client.HttpClient;

import java.net.URI;
import java.util.Map;
import java.util.function.Supplier;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.*;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public final class AlertNotificationConfigurationClient extends AbstractClient implements IAlertNotificationConfigurationClient {

    private HttpClient httpClient;
    private final IRetryPolicy retryPolicy;
    private final ServiceRegion serviceRegion;
    private IAuthorizationHeader authorizationHeader;
    private final URI actionBaseUri;
    private final URI conditionBaseUri;
    private final URI subscriptionBaseUri;

    public AlertNotificationConfigurationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            IAuthorizationHeader authorizationHeader
    ) {
        super(httpClient, authorizationHeader, null, null, null, null, false);

        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
        this.authorizationHeader = authorizationHeader;
        this.actionBaseUri = buildActionsUri(serviceRegion, emptyMap());
        this.conditionBaseUri = buildConditionsUri(serviceRegion, emptyMap());
        this.subscriptionBaseUri = buildSubscriptionsUri(serviceRegion, emptyMap());
    }

    public AlertNotificationConfigurationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            IAuthorizationHeader authorizationHeader,
            Long invalidationTime,
            KeyStoreDetails keyStoreDetails,
            DestinationCredentialsProvider destinationCredentialsProvider,
            HttpClientFactory httpClientFactory,
            boolean isCertificateAuthentication
    ) {
        super(httpClient, authorizationHeader, invalidationTime, keyStoreDetails, destinationCredentialsProvider, httpClientFactory, isCertificateAuthentication);

        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
        this.authorizationHeader = authorizationHeader;
        this.actionBaseUri = buildActionsUri(serviceRegion, emptyMap());
        this.conditionBaseUri = buildConditionsUri(serviceRegion, emptyMap());
        this.subscriptionBaseUri = buildSubscriptionsUri(serviceRegion, emptyMap());
    }

    public AlertNotificationConfigurationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            String certificateChain,
            String privateKey,
            HttpClientFactory httpClientFactory,
            boolean isCertificateAuthentication
    ) {
        super(httpClient, certificateChain, privateKey, httpClientFactory, isCertificateAuthentication);

        this.httpClient = httpClient;
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
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
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildConditionsUri(serviceRegion, queryParameters))), CONDITION_CONFIGURATION_TYPE);
    }

    @Override
    public Condition createCondition(Condition condition) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(conditionBaseUri, toJsonString(condition))), CONDITION_TYPE);
    }

    @Override
    public Condition getCondition(String conditionName) {
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildConditionUri(serviceRegion, conditionName))), CONDITION_TYPE);
    }

    @Override
    public Condition updateCondition(Condition condition) {
        return fromJsonString(executeWithRetry(() -> executeHttpPut(buildConditionUri(serviceRegion, condition.getName()), toJsonString(condition))), CONDITION_TYPE);
    }

    @Override
    public void deleteCondition(String conditionName) {
        executeWithRetry(() -> executeHttpDelete(buildConditionUri(serviceRegion, conditionName)));
    }

    @Override
    public ConfigurationResponse<Action> getActions(Map<ConfigurationQueryParameter, String> queryParameters) {
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildActionsUri(serviceRegion, queryParameters))), ACTION_CONFIGURATION_TYPE);
    }

    @Override
    public Action createAction(Action action) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(actionBaseUri, toJsonString(action))), ACTION_TYPE);
    }

    @Override
    public Action getAction(String actionName) {
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildActionUri(serviceRegion, actionName))), ACTION_TYPE);
    }

    @Override
    public Action updateAction(Action action) {
        return fromJsonString(executeWithRetry(() -> executeHttpPut(buildActionUri(serviceRegion, action.getName()), toJsonString(action))), ACTION_TYPE);
    }

    @Override
    public void deleteAction(String actionName) {
        executeWithRetry(() -> executeHttpDelete(buildActionUri(serviceRegion, actionName)));
    }

    @Override
    public ConfigurationResponse<Subscription> getSubscriptions(Map<ConfigurationQueryParameter, String> queryParameters) {
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildSubscriptionsUri(serviceRegion, queryParameters))), SUBSCRIPTION_CONFIGURATION_TYPE);
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        return fromJsonString(executeWithRetry(() -> executeHttpPost(subscriptionBaseUri, toJsonString(subscription))), SUBSCRIPTION_TYPE);
    }

    @Override
    public Subscription getSubscription(String subscriptionName) {
        return fromJsonString(executeWithRetry(() -> executeHttpGet(buildSubscriptionUri(serviceRegion, subscriptionName))), SUBSCRIPTION_TYPE);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        return fromJsonString(executeWithRetry(() -> executeHttpPut(buildSubscriptionUri(serviceRegion, subscription.getName()), toJsonString(subscription))), SUBSCRIPTION_TYPE);
    }

    @Override
    public void deleteSubscription(String subscriptionName) {
        executeWithRetry(() -> executeHttpDelete(buildSubscriptionUri(serviceRegion, subscriptionName)));
    }

    @Override
    public Configuration importConfiguration(Configuration newConfiguration) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpPost(buildConfigurationManagementUri(serviceRegion), toJsonString(newConfiguration))), CONFIGURATION_TYPE);
    }

    @Override
    public Configuration exportConfiguration() {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(buildConfigurationManagementUri(serviceRegion))), CONFIGURATION_TYPE);
    }

    private String executeWithRetry(Supplier<String> supplier) {
       return retryPolicy.executeWithRetry(supplier);
    }
}