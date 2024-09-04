package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.Action;
import com.sap.cloud.alert.notification.client.model.configuration.State;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.SetUtils.emptyIfNull;
import static org.apache.http.util.TextUtils.isBlank;

public final class ActionBuilder {

    public static final State DEFAULT_STATE = ENABLED;

    private String type;
    private String name;
    private State state = DEFAULT_STATE;
    private String description;
    private Set<String> labels = new HashSet<>();
    private Integer discardAfter;
    private Integer fallbackTime;
    private String fallbackAction;
    private Boolean enableDeliveryStatus;
    private Map<String, String> properties = new HashMap<>();

    public ActionBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public ActionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ActionBuilder withState(State state) {
        this.state = state;
        return this;
    }

    public ActionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ActionBuilder withLabel(String label) {
        if (!isBlank(label)) {
            this.labels.add(label);
        }

        return this;
    }

    public ActionBuilder withLabels(Set<String> labels) {
        emptyIfNull(labels).forEach(this::withLabel);

        return this;
    }

    public ActionBuilder withDiscardAfter(Integer discardAfter) {
        this.discardAfter = discardAfter;
        return this;
    }

    public ActionBuilder withFallbackTime(Integer fallbackTime) {
        this.fallbackTime = fallbackTime;
        return this;
    }

    public ActionBuilder withFallbackAction(String fallbackAction) {
        this.fallbackAction = fallbackAction;
        return this;
    }

    public ActionBuilder withEnableDeliveryStatus(Boolean enableDeliveryStatus) {
        this.enableDeliveryStatus = enableDeliveryStatus;
        return this;
    }

    public ActionBuilder withProperty(String key, String value) {
        if (!isBlank(value)) {
            this.properties.put(key, value);
        }

        return this;
    }

    public ActionBuilder withProperties(Map<String, String> properties) {
        MapUtils.emptyIfNull(properties).forEach(this::withProperty);

        return this;
    }

    public Action build() {
        return new Action(
                requireNonNull(type),
                requireNonNull(name),
                requireNonNull(state),
                description,
                labels,
                discardAfter,
                fallbackTime,
                fallbackAction,
                enableDeliveryStatus,
                properties
        );
    }
}
