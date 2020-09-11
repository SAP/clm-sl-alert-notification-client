package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationResponseTest {

    private static final String TEST_CONFIGURATION_RESPONSE_AS_JSON_STRING = "{" 
            + String.format("\"metadata\":%s,", toJsonString(TEST_CONFIGURATION_PAGING_METADATA))
            + String.format("\"results\":%s", toJsonString(singletonList(TEST_CONDITION)))
            + "}";

    private ConfigurationResponse<Condition> classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConfigurationResponse<>(singletonList(TEST_CONDITION), TEST_CONFIGURATION_PAGING_METADATA);
    }

    @Test
    public void whenConfigurationResponseIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_CONFIGURATION_RESPONSE_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void whenConfigurationResponseIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_CONFIGURATION_RESPONSE_AS_JSON_STRING, CONDITION_CONFIGURATION_TYPE));
    }

    @Test
    public void whenGetMetadataIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_CONFIGURATION_PAGING_METADATA, classUnderTest.getMetadata());
    }

    @Test
    public void whenGetResultsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(singletonList(TEST_CONDITION), classUnderTest.getResults());
    }

    @Test
    public void givenThatResultsAreNull_whenConstructorIsInvoked_thenResultsAreSetToAnEmptyCollection() {
        classUnderTest = new ConfigurationResponse<>(null, TEST_CONFIGURATION_PAGING_METADATA);

        assertEquals(emptyList(), classUnderTest.getResults());
    }
}
