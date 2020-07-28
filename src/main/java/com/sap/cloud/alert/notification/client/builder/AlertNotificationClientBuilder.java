package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.internal.*;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class AlertNotificationClientBuilder {

    private HttpClient httpClient;
    private IRetryPolicy retryPolicy;
    private ServiceRegion serviceRegion;
    private IAuthorizationHeader authorizationHeader;

    public AlertNotificationClientBuilder(HttpClient httpClient) {
        this.serviceRegion = null;
        this.authorizationHeader = null;
        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = new SimpleRetryPolicy();
    }

    public AlertNotificationClientBuilder withRetryPolicy(IRetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;

        return this;
    }

    public AlertNotificationClientBuilder withServiceRegion(ServiceRegion serviceRegion) {
        this.serviceRegion = serviceRegion;

        return this;
    }

    public AlertNotificationClientBuilder withAuthentication(String clientId, String clientSecret) {
        this.authorizationHeader = new BasicAuthorizationHeader(clientId, clientSecret);

        return this;
    }

    public AlertNotificationClientBuilder withAuthentication(String clientId, String clientSecret, URI oAuthServiceURI) {
        this.authorizationHeader = new OAuthAuthorizationHeader(clientId, clientSecret, oAuthServiceURI, httpClient);

        return this;
    }

    public AlertNotificationClient build() {
        return new AlertNotificationClient(
                requireNonNull(httpClient),
                requireNonNull(retryPolicy),
                requireNonNull(serviceRegion),
                requireNonNull(authorizationHeader)
        );
    }
}