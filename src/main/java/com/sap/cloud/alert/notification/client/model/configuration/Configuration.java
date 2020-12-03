package com.sap.cloud.alert.notification.client.model.configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;


import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class Configuration {

    private final Set<Action> actions;
    private final Set<Condition> conditions;
    private final Set<Subscription> subscriptions;

    @JsonCreator
    public Configuration( //
            @JsonProperty("actions") Collection<Action> actions, //
            @JsonProperty("conditions") Collection<Condition> conditions, //
            @JsonProperty("subscriptions") Collection<Subscription> subscriptions //
    ) {
        this.actions = new HashSet<>(emptyIfNull(actions));
        this.conditions = new HashSet<>(emptyIfNull(conditions));
        this.subscriptions = new HashSet<>(emptyIfNull(subscriptions));
    }

    @JsonProperty("actions")
    public Set<Action> getActions() {
        return unmodifiableSet(actions);
    }

    @JsonProperty("conditions")
    public Set<Condition> getConditions() {
        return unmodifiableSet(conditions);
    }

    @JsonProperty("subscriptions")
    public Set<Subscription> getSubscriptions() {
        return unmodifiableSet(subscriptions);
    }
}