package com.sap.cloud.alert.notification.client.internal;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class BasicAuthorizationHeader implements IAuthorizationHeader {

    private static final String TEMPLATE_BASE64_ENCODING = "%s:%s";
    private static final String TEMPLATE_AUTHORIZATION_HEADER_BASIC_VALUE = "Basic %s";

    private final byte basicCredentials[];

    public BasicAuthorizationHeader(String username, String password) {
        requireNonNull(username);
        requireNonNull(password);

        basicCredentials = getEncodedCredentials(username, password).getBytes(UTF_8);
    }

    @Override
    public String getValue() {
        return format(TEMPLATE_AUTHORIZATION_HEADER_BASIC_VALUE, new String(basicCredentials, UTF_8));
    }

    private String getEncodedCredentials(String username, String password) {
        return encodeBase64String(format(TEMPLATE_BASE64_ENCODING, username, password).getBytes(UTF_8)).trim();
    }
}