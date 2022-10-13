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
    private static final String CERTIFICATE = "certificate";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String ALERT_NOTIFICATION_SERVICE_LABEL = "alert-notification";

    private final URI serviceUri;
    private final URI oauthUri;
    private final String clientId;
    private final String clientSecret;
    private String certificate;
    private String privateKey;

    public AlertNotificationServiceBinding() {
        this(new CfEnv().findCredentialsByLabel(ALERT_NOTIFICATION_SERVICE_LABEL));
    }

    protected AlertNotificationServiceBinding(CfCredentials credentials) {
        this(create(credentials.getString(URL)), //
                isNull(credentials.getString(OAUTH_URL)) ? null : create(credentials.getString(OAUTH_URL)), //
                credentials.getString(CLIENT_ID), //
                credentials.getString(CLIENT_SECRET), //
                credentials.getString(CERTIFICATE), //
                credentials.getString(PRIVATE_KEY)
        );
    }

    private AlertNotificationServiceBinding(URI serviceUri, URI oauthUri, String clientId, String clientSecret, String certificate, String privateKey) {
        this.serviceUri = serviceUri;
        this.oauthUri = oauthUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    public AlertNotificationServiceBinding(URI serviceUri, URI oauthUri, String clientId, String clientSecret) {
     this(serviceUri, oauthUri, clientId, clientSecret, null, null);
    }

    public AlertNotificationServiceBinding(URI serviceUri, URI oauthUri, String clientId, String certificate, String privateKey) {
        this(serviceUri, oauthUri, clientId, null, certificate, privateKey);
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

    public String getCertificate() {
        return certificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
