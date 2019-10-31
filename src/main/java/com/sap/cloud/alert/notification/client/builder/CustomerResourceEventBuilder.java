package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.AffectedCustomerResource;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.EventCategory;
import com.sap.cloud.alert.notification.client.model.EventSeverity;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class CustomerResourceEventBuilder {

    private String type;
    private String body;
    private String subject;
    private Integer priority;
    private Long eventTimestamp;
    private EventSeverity severity;
    private EventCategory category;
    private Map<String, String> tags;
    private AffectedCustomerResource resource;

    public CustomerResourceEventBuilder() {
        this.type = null;
        this.body = null;
        this.subject = null;
        this.priority = null;
        this.severity = null;
        this.category = null;
        this.resource = null;
        this.eventTimestamp = null;
        this.tags = new HashMap<>();
    }

    public CustomerResourceEventBuilder withType(String type) {
        this.type = type;

        return this;
    }

    public CustomerResourceEventBuilder withEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;

        return this;
    }

    public CustomerResourceEventBuilder withSeverity(EventSeverity severity) {
        this.severity = severity;

        return this;
    }

    public CustomerResourceEventBuilder withCategory(EventCategory category) {
        this.category = category;

        return this;
    }

    public CustomerResourceEventBuilder withSubject(String subject) {
        this.subject = subject;

        return this;
    }

    public CustomerResourceEventBuilder withBody(String body) {
        this.body = body;

        return this;
    }

    public CustomerResourceEventBuilder withPriority(Integer priority) {
        this.priority = priority;

        return this;
    }

    public CustomerResourceEventBuilder withTags(Map<String, String> tags) {
        this.tags = new HashMap<>(emptyIfNull(tags));

        return this;
    }

    public CustomerResourceEventBuilder withAffectedResource(AffectedCustomerResource affectedResource) {
        this.resource = affectedResource;

        return this;
    }

    public CustomerResourceEvent build() {
        return new CustomerResourceEvent(
                null,
                requireNonNull(type),
                eventTimestamp,
                requireNonNull(severity),
                requireNonNull(category),
                priority,
                requireNonNull(subject),
                requireNonNull(body),
                tags,
                requireNonNull(resource)
        );
    }
}
