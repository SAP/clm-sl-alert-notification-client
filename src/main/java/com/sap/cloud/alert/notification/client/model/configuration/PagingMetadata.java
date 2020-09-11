package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@ToString(doNotUseGetters = true)
@JsonPropertyOrder(alphabetic = true)
@EqualsAndHashCode(doNotUseGetters = true)
public final class PagingMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer page;
    private final Long totalPages;
    private final Integer pageSize;
    private final Long totalCount;

    @JsonCreator
    public PagingMetadata(
            @JsonProperty("page") Integer page,
            @JsonProperty("pageSize") Integer pageSize,
            @JsonProperty("totalPages") Long totalPages,
            @JsonProperty("totalCount") Long totalCount
    ) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }

    @JsonProperty("page")
    public Integer getPage() {
        return page;
    }

    @JsonProperty("pageSize")
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("totalPages")
    public Long getTotalPages() {
        return totalPages;
    }

    @JsonProperty("totalCount")
    public Long getTotalCount() {
        return totalCount;
    }
}
