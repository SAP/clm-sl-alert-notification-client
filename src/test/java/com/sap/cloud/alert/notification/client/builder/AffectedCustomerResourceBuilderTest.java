package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.AffectedCustomerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AffectedCustomerResourceBuilderTest {

    private static final String TEST_RESOURCE_NAME = "TEST_RESOURCE_NAME";
    private static final String TEST_RESOURCE_TYPE = "TEST_RESOURCE_TYPE";
    private static final String TEST_RESOURCE_INSTANCE = "TEST_RESOURCE_INSTANCE";
    private static final Map<String, String> TEST_RESOURCE_TAGS = singletonMap("TEST_KEY", "TEST_VALUE");

    private AffectedCustomerResourceBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new AffectedCustomerResourceBuilder();
    }

    @Test
    public void whenBuilderIsUsed_thenCorrectAffectedResourceIsBuild() {
        AffectedCustomerResource expectedResource = new AffectedCustomerResource(TEST_RESOURCE_NAME, TEST_RESOURCE_TYPE, TEST_RESOURCE_INSTANCE, TEST_RESOURCE_TAGS);
        AffectedCustomerResource createdResource = classUnderTest
                .withName(TEST_RESOURCE_NAME)
                .withTags(TEST_RESOURCE_TAGS)
                .withType(TEST_RESOURCE_TYPE)
                .withInstance(TEST_RESOURCE_INSTANCE)
                .build();

        assertEquals(expectedResource.getTags(), createdResource.getTags());
        assertEquals(expectedResource.getResourceName(), createdResource.getResourceName());
        assertEquals(expectedResource.getResourceType(), createdResource.getResourceType());
        assertEquals(expectedResource.getResourceInstance(), createdResource.getResourceInstance());
    }

    @Test
    public void givenThatNameIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withName(null)
                    .withTags(TEST_RESOURCE_TAGS)
                    .withType(TEST_RESOURCE_TYPE)
                    .withInstance(TEST_RESOURCE_INSTANCE)
                    .build();
        });
    }

    @Test
    public void givenThatTypeIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withType(null)
                    .withName(TEST_RESOURCE_NAME)
                    .withTags(TEST_RESOURCE_TAGS)
                    .withInstance(TEST_RESOURCE_INSTANCE)
                    .build();
        });
    }
}
