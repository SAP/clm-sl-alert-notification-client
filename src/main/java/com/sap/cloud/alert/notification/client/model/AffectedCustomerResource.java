package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AffectedCustomerResource {

    private final String name;
    private final String type;
    private final String instance;
    private final Map<String, String> tags;

    @JsonCreator
    public AffectedCustomerResource(
            @JsonProperty("resourceName") String name,
            @JsonProperty("resourceType") String type,
            @JsonProperty("resourceInstance") String instance,
            @JsonProperty("tags") Map<String, String> tags
    ) {
        this.instance = instance;
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
        this.tags = new HashMap<>(emptyIfNull(tags));
    }

    public String getResourceName() {
        return name;
    }

    public String getResourceType() {
        return type;
    }

    public String getResourceInstance() {
        return instance;
    }

    public Map<String, String> getTags() {
        return unmodifiableMap(tags);
    }
}
