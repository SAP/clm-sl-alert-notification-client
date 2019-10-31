package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageMetadata {

    private final Integer page;
    private final Integer pageSize;
    private final Long totalPages;
    private final Long totalResultsCount;

    @JsonCreator
    public PageMetadata(
            @JsonProperty("page") Integer page,
            @JsonProperty("pageSize") Integer pageSize,
            @JsonProperty("totalPages") Long totalPages,
            @JsonProperty("totalResultsCount") Long totalResultsCount
    ) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalResultsCount = totalResultsCount;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public Long getTotalResultsCount() {
        return totalResultsCount;
    }
}
