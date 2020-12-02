package com.sap.cloud.alert.notification.client.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(doNotUseGetters = true)
@JsonPropertyOrder(alphabetic = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class ConfigurationErrorResponse {

    private final int code;
    private final String message;

    @JsonCreator
    public ConfigurationErrorResponse(
            @JsonProperty(required = true, value = "code") int code,
            @JsonProperty(required = true, value = "message") String message
    ) {
        this.code = code;
        this.message = message;
    }

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

}
