package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.State;
import com.sap.cloud.alert.notification.client.model.configuration.Subscription;

import java.util.HashSet;
import java.util.Set;

import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.SetUtils.emptyIfNull;
import static org.apache.http.util.TextUtils.isBlank;

public final class SubscriptionBuilder {

    public static final State DEFAULT_STATE = ENABLED;

    private String name;
    private State state = DEFAULT_STATE;
    private Long snoozeTimestamp;
    private String description;
    private Set<String> labels = new HashSet<>();
    private Set<String> actions = new HashSet<>();
    private Set<String> conditions = new HashSet<>();

    public SubscriptionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SubscriptionBuilder withState(State state) {
        this.state = state;
        return this;
    }

    public SubscriptionBuilder withSnoozeTimestamp(Long snoozeTimestamp) {
        this.snoozeTimestamp = snoozeTimestamp;
        return this;
    }

    public SubscriptionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SubscriptionBuilder withLabel(String label) {
        if (!isBlank(label)) {
            this.labels.add(label);
        }

        return this;
    }

    public SubscriptionBuilder withLabels(Set<String> labels) {
        emptyIfNull(labels).forEach(this::withLabel);

        return this;
    }

    public SubscriptionBuilder withAction(String action) {
        if (!isBlank(action)) {
            this.actions.add(action);
        }

        return this;
    }

    public SubscriptionBuilder withActions(Set<String> actions) {
        emptyIfNull(actions).forEach(this::withAction);

        return this;
    }

    public SubscriptionBuilder withCondition(String condition) {
        if (!isBlank(condition)) {
            this.conditions.add(condition);
        }

        return this;
    }

    public SubscriptionBuilder withConditions(Set<String> conditions) {
        emptyIfNull(conditions).forEach(this::withCondition);

        return this;
    }

    public Subscription build() {
        return new Subscription(
                requireNonNull(name),
                requireNonNull(state),
                snoozeTimestamp,
                description,
                labels,
                actions,
                conditions
        );
    }
}
