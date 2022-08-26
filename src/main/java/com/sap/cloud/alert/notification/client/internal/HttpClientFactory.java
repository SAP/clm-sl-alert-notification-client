package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

import static com.sap.cloud.alert.notification.client.internal.KeyStoreUtils.buildKeyStore;

public class HttpClientFactory {

    private static final String EMPTY_STRING = "";
    public HttpClientFactory() {
    }

    public HttpClient createHttpClient(KeyStoreDetails keyStoreDetails) {
        return HttpClients.custom() //
                .setSSLContext(buildSSLContext(buildKeyStore(keyStoreDetails), keyStoreDetails)) //
                .setSSLHostnameVerifier(new DefaultHostnameVerifier()) //
                .build();
    }

    private SSLContext buildSSLContext(KeyStore keyStore, KeyStoreDetails keyStoreDetails) {
        try {
            return SSLContexts.custom() //
                    .loadKeyMaterial(keyStore, keyStoreDetails.getKeyStorePassword().toCharArray()) //
                    .build();
        }catch (Exception e) {
            throw new ClientRequestException("Failed to create ssl context", e);
        }
    }

    public HttpClient createHttpClient(String certificate, String privateKey) {
        return HttpClients.custom() //
                .setSSLContext(buildSSLContext(buildKeyStore(certificate, privateKey))) //
                .setSSLHostnameVerifier(new DefaultHostnameVerifier()) //
                .build();
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
