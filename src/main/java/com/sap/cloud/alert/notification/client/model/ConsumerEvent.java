package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class ConsumerEvent {

    private final String id;
    private final String body;
    private final String type;
    private final String region;
    private final String subject;
    private final Long timestamp;
    private final Integer priority;
    private final String regionType;
    private final EventSeverity severity;
    private final EventCategory category;
    private final Map<String, String> tags;
    private final ConsumerMetadata metadata;
    private final AffectedCustomerResource resource;

    @JsonCreator
    public ConsumerEvent(
            @JsonProperty("id") String id,
            @JsonProperty("eventType") String type,
            @JsonProperty("eventTimestamp") Long timestamp,
            @JsonProperty("severity") EventSeverity severity,
            @JsonProperty("category") EventCategory category,
            @JsonProperty("priority") Integer priority,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body,
            @JsonProperty("tags") Map<String, String> tags,
            @JsonProperty("resource") AffectedCustomerResource resource,
            @JsonProperty("region") String region,
            @JsonProperty("regionType") String regionType,
            @JsonProperty("metadata") ConsumerMetadata metadata
    ) {
        this.id = id;
        this.type = type;
        this.body = body;
        this.region = region;
        this.subject = subject;
        this.severity = severity;
        this.category = category;
        this.priority = priority;
        this.resource = resource;
        this.metadata = metadata;
        this.timestamp = timestamp;
        this.regionType = regionType;
        this.tags = new HashMap<>(emptyIfNull(tags));
    }

    public String getId() {
        return id;
    }

    public String getEventType() {
        return type;
    }

    public Long getEventTimestamp() {
        return timestamp;
    }

    public EventSeverity getSeverity() {
        return severity;
    }

    public EventCategory getCategory() {
        return category;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getRegion() {
        return region;
    }

    public String getRegionType() {
        return regionType;
    }

    public ConsumerMetadata getMetadata() {
        return metadata;
    }

    public Map<String, String> getTags() {
        return new HashMap<>(tags);
    }

    public AffectedCustomerResource getResource() {
        return resource;
    }
}