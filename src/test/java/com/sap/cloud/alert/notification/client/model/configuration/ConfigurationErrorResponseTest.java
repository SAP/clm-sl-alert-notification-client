package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationErrorResponseTest {

    private static final String TEST_CONFIGURATION_ERROR_RESPONSE_AS_JSON_STRING = "{"
            + format("\"code\":%d,", TEST_HTTP_ERROR_CODE)
            + format("\"message\":%s", toJsonString(TEST_MESSAGE))
            + "}";

    private ConfigurationErrorResponse classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConfigurationErrorResponse(
                TEST_HTTP_ERROR_CODE,
                TEST_MESSAGE
        );
    }

    @Test
    public void whenConfigurationErrorResponseIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_CONFIGURATION_ERROR_RESPONSE_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void whenConfigurationErrorResponseIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_CONFIGURATION_ERROR_RESPONSE_AS_JSON_STRING, ConfigurationErrorResponse.class));
    }

    @Test
    public void whenGetCodeIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_HTTP_ERROR_CODE, classUnderTest.getCode());
    }

    @Test
    public void whenGetMessageIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_MESSAGE, classUnderTest.getMessage());
    }

}
