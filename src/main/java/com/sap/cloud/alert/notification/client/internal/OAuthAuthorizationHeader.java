package com.sap.cloud.alert.notification.client.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URI;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.buildCertOAuthServiceUri;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class OAuthAuthorizationHeader implements IAuthorizationHeader {

    protected static final String TOKEN_KEY_EXPIRES_IN = "expires_in";
    protected static final String TOKEN_KEY_ACCESS_TOKEN = "access_token";
    protected static final String TEMPLATE_AUTHORIZATION_HEADER_BEARER_VALUE = "Bearer %s";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static final int TIME_DELTA_IN_SECONDS = 60;
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    private final URI oAuthTokenUri;
    private  HttpClient httpClient;
    private String certificate;
    private String privateKey;
    private  HttpClientFactory httpClientFactory;
    private  IAuthorizationHeader authorizationHeader;

    private byte[] currentOAuthToken;
    private long tokenValidityTimestamp;

    public OAuthAuthorizationHeader(String clientId, String clientSecret, URI uaaApiURI, HttpClient httpClient) {
        this.httpClient = requireNonNull(httpClient);
        this.oAuthTokenUri = requireNonNull(uaaApiURI);
        this.authorizationHeader = new BasicAuthorizationHeader(clientId, clientSecret);
    }

    public OAuthAuthorizationHeader(String certificate, String privateKey, URI oAuthServiceUri, String clientId, HttpClientFactory httpClientFactory) {
        this.oAuthTokenUri = buildCertOAuthServiceUri(oAuthServiceUri, clientId);
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.httpClientFactory = httpClientFactory;
        this.httpClient = this.httpClientFactory.createHttpClient(certificate, privateKey);
    }

    @Override
    public synchronized String getValue() {
        if (isTokenExpired()) {
            renewToken();
        }

        return format(TEMPLATE_AUTHORIZATION_HEADER_BEARER_VALUE, new String(currentOAuthToken, UTF_8));
    }

    private boolean isTokenExpired() {
        return tokenValidityTimestamp <= currentTimeSeconds();
    }

    private void renewToken() {
        JsonNode response = executeRequest(createRenewTokenRequest());

        currentOAuthToken = response.get(TOKEN_KEY_ACCESS_TOKEN).asText().getBytes(UTF_8);
        tokenValidityTimestamp = currentTimeSeconds() + (response.get(TOKEN_KEY_EXPIRES_IN).asLong() - TIME_DELTA_IN_SECONDS);
    }

    private JsonNode executeRequest(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            adjustHttpClient();

            response = httpClient.execute(request);

            AlertNotificationClientUtils.assertSuccessfulResponse(response);

            return JSON_OBJECT_MAPPER.readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new ClientRequestException(e);
        } finally {
            AlertNotificationClientUtils.consumeQuietly(response);
        }
    }

    private HttpUriRequest createRenewTokenRequest() {
        HttpPost httpRequest = new HttpPost(oAuthTokenUri);

        if (isNull(authorizationHeader)){
            httpRequest.setHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CONTENT_TYPE);

            return httpRequest;
        }
        httpRequest.setHeader(AUTHORIZATION, authorizationHeader.getValue());

        return httpRequest;
    }

    private void setSSLContext() {
        httpClient = httpClientFactory.createHttpClient(certificate, privateKey);
    }

    private void adjustHttpClient() {
        if (isNull(authorizationHeader)) {
            setSSLContext();
        }
    }

    private static long currentTimeSeconds() {
        return currentTimeMillis() / 1000;
    }
}
