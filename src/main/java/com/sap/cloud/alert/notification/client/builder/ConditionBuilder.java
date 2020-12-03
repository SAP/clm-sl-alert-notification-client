package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.Condition;
import com.sap.cloud.alert.notification.client.model.configuration.Predicate;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.SetUtils.emptyIfNull;
import static org.apache.http.util.TextUtils.isBlank;

public final class ConditionBuilder {

    private String name;
    private String description;
    private String propertyKey;
    private Predicate predicate;
    private String propertyValue;
    private Boolean mandatory;
    private Set<String> labels = new HashSet<>();

    public ConditionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ConditionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ConditionBuilder withPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
        return this;
    }

    public ConditionBuilder withPredicate(Predicate predicate) {
        this.predicate = predicate;
        return this;
    }

    public ConditionBuilder withPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    public ConditionBuilder withMandatory(Boolean mandatory){
        this.mandatory = mandatory;
        return this;
    }

    public ConditionBuilder withLabel(String label) {
        if (!isBlank(label)) {
            this.labels.add(label);
        }

        return this;
    }

    public ConditionBuilder withLabels(Set<String> labels) {
        emptyIfNull(labels).forEach(this::withLabel);

        return this;
    }

    public Condition build() {
        return new Condition(
                requireNonNull(name),
                description,
                requireNonNull(propertyKey),
                requireNonNull(predicate),
                propertyValue,
                mandatory,
                labels
        );
    }
}
