package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

@ToString(exclude = "properties", doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, exclude = { "timeCreated", "lastModified" })
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String type;
    private final String name;
    private final State state;
    private final String description;
    private final Set<String> labels;
    private final Integer fallbackTime;
    private final String fallbackAction;
    private final Map<String, String> properties;

    private final Long timeCreated;
    private final Long lastModified;

    @JsonCreator
    public Action( 
            @JsonProperty("id") String id, 
            @JsonProperty("type") String type, 
            @JsonProperty("name") String name, 
            @JsonProperty("state") State state, 
            @JsonProperty("description") String description, 
            @JsonProperty("labels") Collection<String> labels, 
            @JsonProperty("fallbackTime") Integer fallbackTime, 
            @JsonProperty("fallbackAction") String fallbackAction, 
            @JsonProperty("properties") Map<String, String> properties, 
            @JsonProperty("timeCreated") Long timeCreated, 
            @JsonProperty("lastModified") Long lastModified 
    ) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.state = state;
        this.description = description;
        this.labels = unmodifiableSet(new HashSet<>(CollectionUtils.emptyIfNull(labels)));
        this.fallbackTime = fallbackTime;
        this.fallbackAction = fallbackAction;
        this.properties = unmodifiableMap(new HashMap<>(emptyIfNull(properties)));
        this.timeCreated = timeCreated;
        this.lastModified = lastModified;
    }

    public Action( 
            String type, 
            String name, 
            State state, 
            String description, 
            Collection<String> labels, 
            Integer fallbackTime, 
            String fallbackAction, 
            Map<String, String> properties 
    ) {
        this(null, type, name, state, description, labels, fallbackTime, fallbackAction, properties, null, null);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("state")
    public State getState() {
        return state;
    }

    @JsonProperty("labels")
    public Collection<String> getLabels() {
        return unmodifiableSet(labels);
    }

    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return unmodifiableMap(properties);
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("fallbackAction")
    public String getFallbackAction() {
        return fallbackAction;
    }

    @JsonProperty("fallbackTime")
    public Integer getFallbackTime() {
        return fallbackTime;
    }

    @JsonProperty("timeCreated")
    public Long getTimeCreated() {
        return timeCreated;
    }

    @JsonProperty("lastModified")
    public Long getLastModified() {
        return lastModified;
    }

}
