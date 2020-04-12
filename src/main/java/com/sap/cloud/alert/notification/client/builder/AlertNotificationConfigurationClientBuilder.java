package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationConfigurationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.internal.*;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public final class AlertNotificationConfigurationClientBuilder {

    public static final IRetryPolicy DEFAULT_RETRY_POLICY = new SimpleRetryPolicy();

    private String username;
    private String password;
    private URI oAuthServiceUri;
    private HttpClient httpClient;
    private ServiceRegion serviceRegion;
    private IRetryPolicy retryPolicy = DEFAULT_RETRY_POLICY;

    public AlertNotificationConfigurationClientBuilder withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withRetryPolicy(IRetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withServiceRegion(ServiceRegion serviceRegion) {
        this.serviceRegion = serviceRegion;

        return this;
    }

    public AlertNotificationConfigurationClientBuilder withAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withAuthentication(String username, String password, URI oAuthServiceUri) {
        this.username = username;
        this.password = password;
        this.oAuthServiceUri = oAuthServiceUri;
        return this;
    }

    public IAlertNotificationConfigurationClient build() {
        return new AlertNotificationConfigurationClient(
                requireNonNull(httpClient),
                requireNonNull(retryPolicy),
                requireNonNull(serviceRegion),
                buildAuthorizationHeader()
        );
    }

    private IAuthorizationHeader buildAuthorizationHeader() {
        if (username == null && password == null) {
            return null; // Rely on HttpClient configuration only
        }

        return oAuthServiceUri == null
                ? new BasicAuthorizationHeader(username, password)
                : new OAuthAuthorizationHeader(username, password, oAuthServiceUri, httpClient);
    }
}
