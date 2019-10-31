package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationClient;
import com.sap.cloud.alert.notification.client.internal.BasicAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.IAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.OAuthAuthorizationHeader;
import net.jodah.failsafe.RetryPolicy;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class AlertNotificationClientBuilder {

    private HttpClient httpClient;
    private RetryPolicy retryPolicy;
    private ServiceRegion serviceRegion;
    private IAuthorizationHeader authorizationHeader;

    public AlertNotificationClientBuilder(HttpClient httpClient) {
        this.serviceRegion = null;
        this.authorizationHeader = null;
        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = new RetryPolicy().withMaxRetries(0);
    }

    public AlertNotificationClientBuilder withRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy.copy();

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