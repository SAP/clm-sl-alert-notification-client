package com.sap.cloud.alert.notification.client.model;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfEnv;

import java.net.URI;

import static java.net.URI.create;
import static java.util.Objects.isNull;

public class AlertNotificationServiceBinding {

    private static final String URL = "url";
    private static final String OAUTH_URL = "oauth_url";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String ALERT_NOTIFICATION_SERVICE_LABEL = "alert-notification";

    private final URI serviceUri;
    private final URI oauthUri;
    private final String clientId;
    private final String clientSecret;

    public AlertNotificationServiceBinding() {
        this(new CfEnv().findCredentialsByLabel(ALERT_NOTIFICATION_SERVICE_LABEL));
    }

    protected AlertNotificationServiceBinding(CfCredentials credentials) {
        this(create(credentials.getString(URL)), //
                isNull(credentials.getString(OAUTH_URL)) ? null : create(credentials.getString(OAUTH_URL)), //
                credentials.getString(CLIENT_ID), //
                credentials.getString(CLIENT_SECRET) //
        );
    }

    public AlertNotificationServiceBinding(URI serviceUri, URI oauthUri, String clientId, String clientSecret) {
        this.serviceUri = serviceUri;
        this.oauthUri = oauthUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public URI getServiceUri() {
        return serviceUri;
    }

    public URI getOauthUri() {
        return oauthUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
