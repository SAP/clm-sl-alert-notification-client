package com.sap.cloud.alert.notification.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

@JsonInclude(Include.NON_NULL)
public class ConsumerMetadata {

    private final Long cacheTime;
    private final String affectedActionId;
    private final DeliveryStatus deliveryStatus;
    private final Collection<FailureReason> failureReasons;

    @JsonCreator
    public ConsumerMetadata(
        @JsonProperty("cacheTime") Long cacheTime,
        @JsonProperty("deliveryStatus") DeliveryStatus deliveryStatus,
        @JsonProperty("affectedActionId") String affectedActionId,
        @JsonProperty("failureReasons") Collection<FailureReason> failureReasons
) {
        this.cacheTime = cacheTime;
        this.deliveryStatus = deliveryStatus;
        this.affectedActionId = affectedActionId;
        this.failureReasons = failureReasons;
    }

    public Long getCacheTime() {
        return cacheTime;
    }

    public String getAffectedActionId() {
        return affectedActionId;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public Collection<FailureReason> getFailureReasons() {
        return failureReasons;
    }
}
