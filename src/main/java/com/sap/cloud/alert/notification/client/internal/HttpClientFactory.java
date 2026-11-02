package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.builder.HttpClientConfig;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

import static com.sap.cloud.alert.notification.client.internal.KeyStoreUtils.buildKeyStore;

public class HttpClientFactory {

    private static final String EMPTY_STRING = "";

    private final HttpClientConfig config;

    public HttpClientFactory(HttpClientConfig config) {
        this.config = config;
    }

    public HttpClient createHttpClient() {
        return buildHttpClient(null);
    }

    public HttpClient createHttpClient(KeyStoreDetails keyStoreDetails) {
        return buildHttpClient(buildSSLContext(buildKeyStore(keyStoreDetails), keyStoreDetails));
    }

    public HttpClient createHttpClient(String certificate, String privateKey) {
        return buildHttpClient(buildSSLContext(buildKeyStore(certificate, privateKey)));
    }

    private HttpClient buildHttpClient(SSLContext sslContext) {
        int connectTimeoutMs = (int) config.getConnectionEstablishTimeout().toMillis();
        int socketTimeoutMs = (int) config.getSocketTimeout().toMillis();
        int connectionRequestTimeoutMs = (int) config.getConnectionObtainTimeout().toMillis();

        RequestConfig requestConfig = RequestConfig.custom() //
                .setConnectTimeout(connectTimeoutMs) //
                .setSocketTimeout(socketTimeoutMs) //
                .setConnectionRequestTimeout(connectionRequestTimeoutMs) //
                .build();

        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(requestConfig);

        if (sslContext != null) {
            builder.setSSLContext(sslContext).setSSLHostnameVerifier(new DefaultHostnameVerifier());
        }

        return builder.build();
    }

    private SSLContext buildSSLContext(KeyStore keyStore, KeyStoreDetails keyStoreDetails) {
        try {
            return SSLContexts.custom() //
                    .loadKeyMaterial(keyStore, keyStoreDetails.getKeyStorePassword().toCharArray()) //
                    .build();
        } catch (Exception e) {
            throw new ClientRequestException("Failed to create ssl context", e);
        }
    }

    private SSLContext buildSSLContext(KeyStore keyStore) {
        try {
            return SSLContexts.custom() //
                    .loadKeyMaterial(keyStore, EMPTY_STRING.toCharArray()) //
                    .build();
        } catch (Exception e) {
            throw new ClientRequestException("Failed to create ssl context", e);
        }
    }
}
