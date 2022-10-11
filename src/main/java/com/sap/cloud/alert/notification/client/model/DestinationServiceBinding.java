package com.sap.cloud.alert.notification.client.model;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfEnv;

import java.net.URI;

import static java.net.URI.create;

public class DestinationServiceBinding {

    private static final String URL = "uri";
    private static final String OAUTH_URL = "url";
    private static final String CLIENT_ID = "clientid";
    private static final String CLIENT_SECRET = "clientsecret";
    private static final String DESTINATION_SERVICE_LABEL = "destination";

    private final URI serviceUri;
    private final URI oauthUri;
    private final String clientId;
    private final String clientSecret;

    public DestinationServiceBinding() {
        this(new CfEnv().findCredentialsByLabel(DESTINATION_SERVICE_LABEL));
    }

    protected DestinationServiceBinding(CfCredentials credentials) {
        this(create(credentials.getString(URL)), //
                create(credentials.getString(OAUTH_URL)), //
                credentials.getString(CLIENT_ID), //
                credentials.getString(CLIENT_SECRET) //
        );
    }

    public DestinationServiceBinding(URI serviceUri, URI oauthUri, String clientId, String clientSecret) {
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
