package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@ToString
@EqualsAndHashCode(doNotUseGetters = true, exclude = { "timeCreated", "lastModified" })
public class Subscription implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final State state;
    private final String description;
    private final Set<String> labels;
    private final Set<String> actions;
    private final Set<String> conditions;

    private final Long timeCreated;
    private final Long lastModified;

    @JsonCreator
    public Subscription( 
            @JsonProperty("id") String id, 
            @JsonProperty("name") String name, 
            @JsonProperty("state") State state, 
            @JsonProperty("description") String description, 
            @JsonProperty("labels") Collection<String> labels, 
            @JsonProperty("actions") Collection<String> actions, 
            @JsonProperty("conditions") Collection<String> conditions, 
            @JsonProperty("timeCreated") Long timeCreated, 
            @JsonProperty("lastModified") Long lastModified 
    ) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.description = description;
        this.labels = unmodifiableSet(new HashSet<>(emptyIfNull(labels)));
        this.actions = unmodifiableSet(new HashSet<>(emptyIfNull(actions)));
        this.conditions = unmodifiableSet(new HashSet<>(emptyIfNull(conditions)));
        this.timeCreated = timeCreated;
        this.lastModified = lastModified;
    }

    public Subscription( 
            String name, 
            State state, 
            String description, 
            Collection<String> labels, 
            Collection<String> actions, 
            Collection<String> conditions 
    ) {
        this(null, name, state, description, labels, actions, conditions, null, null);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("state")
    public State getState() {
        return state;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("labels")
    public Set<String> getLabels() {
        return unmodifiableSet(labels);
    }

    @JsonProperty("actions")
    public Set<String> getActions() {
        return unmodifiableSet(actions);
    }

    @JsonProperty("conditions")
    public Set<String> getConditions() {
        return unmodifiableSet(conditions);
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
