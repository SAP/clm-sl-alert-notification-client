package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class PagedResponse {

    private final PageMetadata metadata;
    private final Collection<ConsumerEvent> results;

    @JsonCreator
    public PagedResponse(
            @JsonProperty("responseMetadata") PageMetadata metadata,
            @JsonProperty("results") Collection<ConsumerEvent> results
    ) {
        this.metadata = metadata;
        this.results = new ArrayList<>(emptyIfNull(results));
    }

    public PageMetadata getMetadata() {
        return metadata;
    }

    public Collection<ConsumerEvent> getResults() {
        return unmodifiableCollection(results);
    }
}
