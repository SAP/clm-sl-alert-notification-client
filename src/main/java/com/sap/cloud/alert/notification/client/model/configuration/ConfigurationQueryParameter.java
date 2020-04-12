package com.sap.cloud.alert.notification.client.model.configuration;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.apache.commons.collections4.SetUtils.unmodifiableSet;

public enum ConfigurationQueryParameter {

    PAGE("page"),
    PAGE_SIZE("pageSize"),
    ACTION_NAME("actionName"),
    CONDITION_NAME("conditionName"),
    SUBSCRIPTION_NAME("subscriptionName");

    public static final Set<ConfigurationQueryParameter> QUERY_VIABLE_PARAMETERS = unmodifiableSet(of(PAGE, PAGE_SIZE).collect(toSet()));

    private final String key;

    ConfigurationQueryParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
