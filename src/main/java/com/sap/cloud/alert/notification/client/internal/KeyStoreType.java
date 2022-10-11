package com.sap.cloud.alert.notification.client.internal;

enum KeyStoreType {

    JKS("JKS"),
    PKCS12("PKCS12"),
    PEM("PEM"),
    PFX("PKCS12");

    private final String technicalName;

    KeyStoreType(String technicalName) {
        this.technicalName = technicalName;
    }

    public String getTechnicalName() {
        return technicalName;
    }
}
