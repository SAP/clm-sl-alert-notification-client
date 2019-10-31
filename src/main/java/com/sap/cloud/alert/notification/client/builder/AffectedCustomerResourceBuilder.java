package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.AffectedCustomerResource;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class AffectedCustomerResourceBuilder {

    private String name;
    private String type;
    private String instance;
    private Map<String, String> tags;

    public AffectedCustomerResourceBuilder() {
        this.name = null;
        this.type = null;
        this.instance = null;
        this.tags = new HashMap<>();
    }

    public AffectedCustomerResourceBuilder withName(String name) {
        this.name = name;

        return this;
    }

    public AffectedCustomerResourceBuilder withType(String type) {
        this.type = type;

        return this;
    }

    public AffectedCustomerResourceBuilder withInstance(String instance) {
        this.instance = instance;

        return this;
    }

    public AffectedCustomerResourceBuilder withTags(Map<String, String> tags) {
        this.tags = new HashMap<>(emptyIfNull(tags));

        return this;
    }

    public AffectedCustomerResource build() {
        return new AffectedCustomerResource(
                requireNonNull(name),
                requireNonNull(type),
                instance,
                tags
        );
    }
}
