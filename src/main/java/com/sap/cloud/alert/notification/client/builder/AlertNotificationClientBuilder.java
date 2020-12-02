package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.internal.*;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static com.sap.cloud.alert.notification.client.Platform.CF;
import static java.util.Objects.isNull;
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

    public AlertNotificationClient buildFromServiceBinding() {
        return buildFromServiceBinding(new AlertNotificationServiceBinding());
    }

    public AlertNotificationClient buildFromServiceBinding(AlertNotificationServiceBinding serviceBinding) {
        AlertNotificationClientBuilder alertNotificationClientBuilder = withServiceRegion(new ServiceRegion(CF, serviceBinding.getServiceUri().toString()));

        return isNull(serviceBinding.getOauthUri()) //
                ? alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret()).build() //
                : alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret(), serviceBinding.getOauthUri()).build();
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