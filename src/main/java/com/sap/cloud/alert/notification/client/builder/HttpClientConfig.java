package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.internal.ObjectUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@EqualsAndHashCode
@Builder(builderClassName = "Builder", builderMethodName = "", toBuilder = true)
public class HttpClientConfig {

    private static final HttpClientConfig DEFAULT_CONFIG = new Builder() //
            .connectionObtainTimeout(Duration.of(30, ChronoUnit.SECONDS)) //
            .connectionEstablishTimeout(Duration.of(30, ChronoUnit.SECONDS)) //
            .socketTimeout(Duration.of(30, ChronoUnit.SECONDS)) //
            .build();

    private final Duration connectionObtainTimeout;
    private final Duration connectionEstablishTimeout;
    private final Duration socketTimeout;

    private HttpClientConfig(Duration connectionObtainTimeout, Duration connectionEstablishTimeout, Duration socketTimeout) {
        this.connectionObtainTimeout = ObjectUtils.requirePositive(connectionObtainTimeout, "connectionObtainTimeout must be positive duration");
        this.connectionEstablishTimeout = ObjectUtils.requirePositive(connectionEstablishTimeout, "connectionEstablishTimeout must be positive duration");
        this.socketTimeout = ObjectUtils.requirePositive(socketTimeout, "socketTimeout must be positive duration");
    }

    public static Builder fromDefaultConfig() {
        return DEFAULT_CONFIG.toBuilder();
    }

    public static class Builder {

        // Change lombok constructor access to private
        private Builder() {
        }
    }
}
