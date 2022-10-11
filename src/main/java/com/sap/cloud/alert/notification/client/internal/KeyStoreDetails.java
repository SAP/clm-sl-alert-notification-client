package com.sap.cloud.alert.notification.client.internal;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class KeyStoreDetails {

    private final KeyStoreType certificateType;
    private final String keyStorePassword;
    private final String keyStoreContent;

    public KeyStoreDetails(KeyStoreType keyStoreType, String keyStorePassword, String keyStoreContent) {
        this.certificateType = keyStoreType;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreContent = keyStoreContent;
    }

    public KeyStoreType getKeyStoreType() {
        return certificateType;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStoreContent() {
        return keyStoreContent;
    }
}
