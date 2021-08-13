package com.sap.cloud.alert.notification.client.exceptions;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class ServerResponseException extends RuntimeException {

    private static final long serialVersionUID = 5611541307804757631L;

    private static final String MESSAGE_FORMAT = "%s: message='%s', statusCode=%d";
    private static final String MESSAGE_FORMAT_WITH_X_VCAP_REQUEST_ID = "%s: message='%s', statusCode=%d, X-Vcap-Request-Id='%s'";

    private Integer statusCode;
    private String xVcapRequestId;

    public ServerResponseException( //
            String msg, //
            Integer statusCode, //
            String xVcapRequestId //
    ) {
        super(msg);
        this.statusCode = statusCode;
        this.xVcapRequestId = xVcapRequestId;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String getxVcapRequestId() {
        return xVcapRequestId;
    }

    @Override
    public String toString() {
        return nonNull(xVcapRequestId) //
                ? format(MESSAGE_FORMAT_WITH_X_VCAP_REQUEST_ID, getClass().getName(), getMessage(), statusCode, xVcapRequestId) //
                : format(MESSAGE_FORMAT, getClass().getName(), getMessage(), statusCode); //
    }
}
