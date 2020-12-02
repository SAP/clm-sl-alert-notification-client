package com.sap.cloud.alert.notification.client.model.configuration;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.apache.commons.collections4.SetUtils.unmodifiableSet;

public enum ConfigurationQueryParameter {

    PAGE("page"),
    PAGE_SIZE("pageSize");

    private final String key;

    ConfigurationQueryParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
