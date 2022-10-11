package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyStoreUtils {

    public static KeyStore buildKeyStore(KeyStoreDetails keyStoreDetails) {
        KeyStoreType keyStoreType = keyStoreDetails.getKeyStoreType();

        try {
            if (keyStoreType == KeyStoreType.PEM) {
                return PemCertificateUtils.generateKeyStore(decodeCertificateContent(keyStoreDetails.getKeyStoreContent()), keyStoreDetails.getKeyStorePassword());
            }

            KeyStore keyStore = KeyStore.getInstance(keyStoreDetails.getKeyStoreType().getTechnicalName());

            try (InputStream in = new ByteArrayInputStream(decodeKeyStoreContent(keyStoreDetails.getKeyStoreContent()))) {
                keyStore.load(in, keyStoreDetails.getKeyStorePassword().toCharArray());
            }
            return keyStore;
        } catch (Exception e) {
            throw new ClientRequestException("Failed to generate keystore", e);
        }
    }

    private static byte[] decodeKeyStoreContent(String keyStoreContent) {
        return Base64.getDecoder().decode(keyStoreContent);
    }

    private static String decodeCertificateContent(String certificateContent) {
        return new String(Base64.getDecoder().decode(certificateContent.getBytes(UTF_8)), UTF_8);
    }
}
