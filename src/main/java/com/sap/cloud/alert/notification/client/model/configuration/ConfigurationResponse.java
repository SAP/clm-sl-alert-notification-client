package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@ToString(doNotUseGetters = true)
@JsonPropertyOrder(alphabetic = true)
@EqualsAndHashCode(doNotUseGetters = true)
public final class ConfigurationResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> results;
    private final PagingMetadata metadata;

    @JsonCreator
    public ConfigurationResponse(
            @JsonProperty("results") List<T> results,
            @JsonProperty("metadata") PagingMetadata metadata
    ) {
        this.metadata = metadata;
        this.results = unmodifiableList(new ArrayList<>(emptyIfNull(results)));
    }

    @JsonProperty("results")
    public List<T> getResults() {
        return unmodifiableList(results);
    }

    @JsonProperty("metadata")
    public PagingMetadata getMetadata() {
        return metadata;
    }
}
