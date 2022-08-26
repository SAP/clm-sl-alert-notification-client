package com.sap.cloud.alert.notification.client.internal;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class DestinationContext {

    private final String serviceUri;
    private final boolean isCertificateAuthentication;

    public DestinationContext(String serviceUri, boolean isCertificateAuthentication) {
        this.serviceUri = serviceUri;
        this.isCertificateAuthentication = isCertificateAuthentication;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public boolean isCertificateAuthentication() {
        return isCertificateAuthentication;
    }
}
