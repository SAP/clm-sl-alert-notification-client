package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationConfigClient;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationConfigClient;
import com.sap.cloud.alert.notification.client.internal.BasicAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.IAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.OAuthAuthorizationHeader;
import net.jodah.failsafe.RetryPolicy;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public final class AlertNotificationConfigClientBuilder {

    public static final RetryPolicy DEFAULT_RETRY_POLICY = new RetryPolicy().withMaxRetries(0);

    private String username;
    private String password;
    private URI oAuthServiceUri;
    private HttpClient httpClient;
    private ServiceRegion serviceRegion;
    private RetryPolicy retryPolicy = DEFAULT_RETRY_POLICY.copy();

    public AlertNotificationConfigClientBuilder withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public AlertNotificationConfigClientBuilder withRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy.copy();
        return this;
    }

    public AlertNotificationConfigClientBuilder withServiceRegion(ServiceRegion serviceRegion) {
        this.serviceRegion = serviceRegion;

        return this;
    }

    public AlertNotificationConfigClientBuilder withAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public AlertNotificationConfigClientBuilder withAuthentication(String username, String password, URI oAuthServiceUri) {
        this.username = username;
        this.password = password;
        this.oAuthServiceUri = oAuthServiceUri;
        return this;
    }

    public IAlertNotificationConfigClient build() {
        return new AlertNotificationConfigClient(
                requireNonNull(httpClient),
                requireNonNull(retryPolicy).copy(),
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
