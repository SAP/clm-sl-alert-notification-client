package com.sap.cloud.alert.notification.client.exceptions;

public class AuthorizationException extends ServerResponseException {

    private static final long serialVersionUID = 1680072165927004872L;

    public AuthorizationException(String msg, int status, String xVcapRequestId) {
        super(msg, status, xVcapRequestId);
    }
}
