package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.*;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class CustomerResourceEvent {

    private static final ValueRange ALLOWED_EVENT_PRIORITY_RANGE = ValueRange.of(1, 1000);

    private final String id;
    private final String body;
    private final String type;
    private final String subject;
    private final Long timestamp;
    private final Integer priority;
    private final EventSeverity severity;
    private final EventCategory category;
    private final Map<String, String> tags;
    private final AffectedCustomerResource resource;

    @JsonCreator
    public CustomerResourceEvent(
            @JsonProperty("id") String id,
            @JsonProperty("eventType") String type,
            @JsonProperty("eventTimestamp") Long timestamp,
            @JsonProperty("severity") EventSeverity severity,
            @JsonProperty("category") EventCategory category,
            @JsonProperty("priority") Integer priority,
            @JsonProperty("subject") String subject,
            @JsonProperty("body") String body,
            @JsonProperty("tags") Map<String, String> tags,
            @JsonProperty("resource") AffectedCustomerResource resource
    ) {
        this.id = id;
        this.tags = new HashMap<>(emptyIfNull(tags));
        this.type = requireNonNull(type);
        this.body = requireNonNull(body);
        this.subject = requireNonNull(subject);
        this.severity = requireNonNull(severity);
        this.category = requireNonNull(category);
        this.resource = requireNonNull(resource);
        this.priority = requireValidPriority(priority);
        this.timestamp = isNull(timestamp) ? Instant.now().getEpochSecond() : timestamp;
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

    public Map<String, String> getTags() {
        return new HashMap<>(tags);
    }

    public AffectedCustomerResource getResource() {
        return resource;
    }

    private static Integer requireValidPriority(Integer priority) {
        if (nonNull(priority) && !ALLOWED_EVENT_PRIORITY_RANGE.isValidValue(priority)) {
            throw new IllegalArgumentException();
        }

        return priority;
    }
}
