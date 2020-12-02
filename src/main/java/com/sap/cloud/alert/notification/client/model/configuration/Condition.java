package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final String propertyKey;
    private final Predicate predicate;
    private final String propertyValue;
    private final Set<String> labels;

    private final Long timeCreated;
    private final Long lastModified;

    @JsonCreator
    public Condition( 
            @JsonProperty("id") String id, 
            @JsonProperty("name") String name, 
            @JsonProperty("description") String description, 
            @JsonProperty("propertyKey") String propertyKey, 
            @JsonProperty("predicate") Predicate predicate, 
            @JsonProperty("propertyValue") String propertyValue, 
            @JsonProperty("labels") Collection<String> labels, 
            @JsonProperty("timeCreated") Long timeCreated, 
            @JsonProperty("lastModified") Long lastModified 
    ) {
        this.id = id;
        this.name = name;
        this.predicate = predicate;
        this.description = description;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.labels = unmodifiableSet(new HashSet<>(emptyIfNull(labels)));
        this.timeCreated = timeCreated;
        this.lastModified = lastModified;
    }

    public Condition( 
            String name, 
            String description, 
            String propertyKey, 
            Predicate predicate, 
            String propertyValue, 
            Collection<String> labels 
    ) {
        this(null, name, description, propertyKey, predicate, propertyValue, labels, null, null);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("propertyKey")
    public String getPropertyKey() {
        return propertyKey;
    }

    @JsonProperty("predicate")
    public Predicate getPredicate() {
        return predicate;
    }

    @JsonProperty("propertyValue")
    public String getPropertyValue() {
        return propertyValue;
    }

    @JsonProperty("labels")
    public Collection<String> getLabels() {
        return unmodifiableSet(labels);
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
