package com.sap.cloud.alert.notification.client.exceptions;

public class ClientRequestException extends RuntimeException {

    private static final long serialVersionUID = 1680072165927004872L;

    public ClientRequestException(Throwable cause) {
        super(cause);
    }

    public ClientRequestException(String message) {
        super(message);
    }

    public ClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
