package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.*;
import static com.sap.cloud.alert.notification.client.internal.KeyStoreUtils.buildKeyStore;
import static java.lang.Boolean.FALSE;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;

public class AbstractClient {

    private static final String FAILED_EXECUTION = "Failed execution";

    private static final long TIME_DELTA_IN_MILLIS = 300000;

    long credentialsLoadTime;
    long certificateExpirationTime = 0L;
    Long invalidationTime;

    private IAuthorizationHeader authorizationHeader;
    private HttpClient httpClient;

    private final HttpClientFactory httpClientFactory;
    private final boolean isCertificateAuthentication;
    private DestinationCredentialsProvider destinationCredentialsProvider;

    public AbstractClient(
           HttpClient httpClient,
           IAuthorizationHeader authorizationHeader,
           Long invalidationTime,
           KeyStoreDetails keyStoreDetails,
           DestinationCredentialsProvider destinationCredentialsProvider,
           HttpClientFactory httpClientFactory,
           boolean isCertificateAuthentication
    ) {
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
        this.invalidationTime = invalidationTime;
        this.isCertificateAuthentication = isCertificateAuthentication;
        this.destinationCredentialsProvider = destinationCredentialsProvider;
        this.credentialsLoadTime = currentTimeMillis();
        this.httpClientFactory = httpClientFactory;

        if (isCertificateAuthentication) {
            this.httpClient = this.httpClientFactory.createHttpClient(keyStoreDetails);
        }
    }

    public AbstractClient(
            HttpClient httpClient,
            String certificateChain,
            String privateKey,
            HttpClientFactory httpClientFactory,
            boolean isCertificateAuthentication
    ) {
        this.httpClient = httpClient;
        this.isCertificateAuthentication = isCertificateAuthentication;
        this.httpClientFactory = httpClientFactory;
        this.httpClient = this.httpClientFactory.createHttpClient(certificateChain, privateKey);
    }

    protected String executeHttpPost(URI serviceUri, String payload) {
        return executeRequest(createPostRequest(serviceUri, payload), SC_CREATED, SC_ACCEPTED);
    }

    protected String executeHttpGet(URI serviceUri) {
        return executeRequest(createGetRequest(serviceUri), SC_OK);
    }

    protected String executeHttpPut(URI serviceUri, String payload) {
        return executeRequest(createPutRequest(serviceUri, payload), SC_OK);
    }

    protected String executeHttpDelete(URI serviceUri) {
        return executeRequest(createDeleteRequest(serviceUri), SC_OK, SC_NO_CONTENT);
    }

    private String executeRequest(HttpUriRequest httpRequest, Integer ...expectedStatuses) {
        HttpResponse response = null;
        try {
            adjustHttpClient();
            response = httpClient.execute(httpRequest);

            assertHttpStatus(response, expectedStatuses);

            return isNull(response.getEntity()) ? EMPTY : EntityUtils.toString(response.getEntity(), UTF_8.name());
        } catch (IOException exception) {
            throw new ClientRequestException(FAILED_EXECUTION, exception);
        } finally {
            consumeQuietly(response);
        }
    }

    private HttpGet createGetRequest(URI serviceUri) {
        HttpGet request = new HttpGet(serviceUri);

        setAuthorizationHeader(request);
        request.setHeader(ACCEPT, APPLICATION_JSON);

        return request;
    }

    private HttpPost createPostRequest(URI serviceUri, String payload) {
        HttpPost request = new HttpPost(serviceUri);

        setAuthorizationHeader(request);
        request.setEntity(toStringEntity(payload));
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);

        return request;
    }

    private HttpPut createPutRequest(URI serviceUri, String payload) {
        HttpPut request = new HttpPut(serviceUri);

        setAuthorizationHeader(request);
        request.setEntity(toStringEntity(payload));
        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);

        return request;
    }

    private HttpDelete createDeleteRequest(URI serviceUri) {
        HttpDelete request = new HttpDelete(serviceUri);

        setAuthorizationHeader(request);

        return request;
    }

    private void setAuthorizationHeader(HttpRequest request) {
        if (isCertificateAuthentication) {
            return;
        }

        if (shouldRefreshCredentials()) {
            authorizationHeader = destinationCredentialsProvider.getAuthorizationHeader();
            credentialsLoadTime = currentTimeMillis();
        }

        request.setHeader(AUTHORIZATION, authorizationHeader.getValue());
    }

    private static void assertHttpStatus(HttpResponse response, Integer... expectedCodes) throws IOException {
        if (!asList(expectedCodes).contains(Integer.valueOf(response.getStatusLine().getStatusCode()))) {
            Header firstHeader = response.getFirstHeader(X_VCAP_REQUEST_ID_HEADER);
            throw new ServerResponseException( //
                    extractMessage(response), //
                    response.getStatusLine().getStatusCode(), //
                    nonNull(firstHeader) ? firstHeader.getValue() : null //
            );
        }
    }

    private StringEntity toStringEntity(String payload) {
        return new StringEntity(payload, UTF_8.name());
    }

    private void adjustHttpClient() {
        if (isCertificateAuthentication && isTimeToReloadCertificate() && nonNull(destinationCredentialsProvider)) {
            setSSLContext();
        }
    }

    private void setSSLContext() {
        KeyStoreDetails keyStoreDetails = destinationCredentialsProvider.getKeyStoreDetails();
        KeyStore keyStore = buildKeyStore(keyStoreDetails);

        this.credentialsLoadTime = currentTimeMillis();
        this.certificateExpirationTime = getCertificateExpirationTimeMs(keyStore);

        this.httpClient = httpClientFactory.createHttpClient(keyStoreDetails);
    }

    private long getCertificateExpirationTimeMs(KeyStore keyStore) {
        try {
            List<String> keyStoreAliases = list(keyStore.aliases());

            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyStoreAliases.get(0));

            return certificate.getNotAfter().getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTimeToReloadCertificate() {
        return nonNull(invalidationTime) && invalidationTime > 0L ?
                invalidationTime * 1000 < (currentTimeMillis() - credentialsLoadTime)
                        || certificateExpirationTime <= currentTimeMillis() :
                TIME_DELTA_IN_MILLIS < (currentTimeMillis() - credentialsLoadTime)
                        || TIME_DELTA_IN_MILLIS > (certificateExpirationTime - currentTimeMillis());
    }

    private boolean shouldRefreshCredentials() {
        return isNull(invalidationTime) ? FALSE : invalidationTime * 1000 < (currentTimeMillis() - credentialsLoadTime);
    }
}
