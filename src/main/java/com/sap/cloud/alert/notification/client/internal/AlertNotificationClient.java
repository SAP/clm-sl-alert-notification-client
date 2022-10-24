package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;
import org.apache.http.client.HttpClient;
import java.util.Map;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.*;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.fromJsonString;
import static java.util.Objects.requireNonNull;

public class AlertNotificationClient extends AbstractClient implements IAlertNotificationClient {

    private final HttpClient httpClient;
    private final IRetryPolicy retryPolicy;
    private final ServiceRegion serviceRegion;
    private IAuthorizationHeader authorizationHeader;

    public AlertNotificationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            IAuthorizationHeader authorizationHeader
    ) {
        super(httpClient, authorizationHeader, null, null, null, null, false);

        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
        this.authorizationHeader = requireNonNull(authorizationHeader);
    }

    public AlertNotificationClient(
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
    }

    public AlertNotificationClient(
            HttpClient httpClient,
            IRetryPolicy retryPolicy,
            ServiceRegion serviceRegion,
            String certificateChain,
            String privateKey,
            HttpClientFactory httpClientFactory,
            boolean isCertificateAuthentication
    ) {
        super(httpClient, certificateChain, privateKey, httpClientFactory, isCertificateAuthentication);

        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = requireNonNull(retryPolicy);
        this.serviceRegion = requireNonNull(serviceRegion);
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
    public CustomerResourceEvent sendEvent(CustomerResourceEvent event) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpPost(buildProducerURI(serviceRegion), toJsonString(event))), CUSTOMER_RESOURCE_EVENT_TYPE);
    }

    @Override
    public PagedResponse getMatchedEvents(Map<QueryParameter, String> queryFilter) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(buildMatchedEventsURI(serviceRegion, queryFilter))), PAGED_RESPONSE_TYPE);
    }

    @Override
    public PagedResponse getMatchedEvent(String eventId, Map<QueryParameter, String> queryFilter) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(buildMatchedEventsURI(serviceRegion, eventId, queryFilter))), PAGED_RESPONSE_TYPE);
    }

    @Override
    public PagedResponse getUndeliveredEvents(Map<QueryParameter, String> queryFilter) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(buildUndeliveredEventsURI(serviceRegion, queryFilter))), PAGED_RESPONSE_TYPE);
    }

    @Override
    public PagedResponse getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryFilter) {
        return fromJsonString(retryPolicy.executeWithRetry(() -> executeHttpGet(buildUndeliveredEventsURI(serviceRegion, eventId, queryFilter))), PAGED_RESPONSE_TYPE);
    }
}
