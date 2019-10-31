package com.sap.cloud.alert.notification.client;

import static java.util.Objects.requireNonNull;

public class Platform {

    public static final Platform CF = new Platform("cf");
    public static final Platform NEO = new Platform("neo");

    private final String key;

    public Platform(String key) {
        this.key = requireNonNull(key);
    }

    public String getKey() {
        return key;
    }
}
