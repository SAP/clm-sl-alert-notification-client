package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AffectedCustomerResourceTest {

    private static final String TEST_RESOURCE_NAME = "TEST_RESOURCE_NAME";
    private static final String TEST_RESOURCE_TYPE = "TEST_RESOURCE_TYPE";
    private static final String TEST_RESOURCE_INSTANCE = "TEST_RESOURCE_INSTANCE";
    private static final Map<String, String> TEST_RESOURCE_TAGS = singletonMap("TEST_KEY", "TEST_VALUE");

    private AffectedCustomerResource classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new AffectedCustomerResource(TEST_RESOURCE_NAME, TEST_RESOURCE_TYPE, TEST_RESOURCE_INSTANCE, TEST_RESOURCE_TAGS);
    }

    @Test
    public void whenGetResourceNameIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_RESOURCE_NAME, classUnderTest.getResourceName());
    }

    @Test
    public void whenGetResourceTypeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_RESOURCE_TYPE, classUnderTest.getResourceType());
    }

    @Test
    public void whenGetResourceInstanceIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_RESOURCE_INSTANCE, classUnderTest.getResourceInstance());
    }

    @Test
    public void whenGetTagsIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_RESOURCE_TAGS, classUnderTest.getTags());
    }
}
