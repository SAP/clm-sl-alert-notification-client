package com.sap.cloud.alert.notification.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceRegionTest {

    private static final Platform TEST_PLATFORM = new Platform("test");
    private static final String TEST_SERVICE_URI = "https://this.url.leads.nowhere.com/";

    private ServiceRegion classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ServiceRegion(TEST_PLATFORM, TEST_SERVICE_URI);
    }

    @Test
    public void givenThatInvalidURIIsProvided_whenCreatingNewServiceRegion_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ServiceRegion(TEST_PLATFORM, "this is not a valid URI");
        });
    }

    @Test
    public void whenGetServiceURIIsCalled_thenCorrectValueIsReturned() {
        assertEquals(URI.create(TEST_SERVICE_URI), classUnderTest.getServiceURI());
    }

    @Test
    public void whenGetPlatformIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PLATFORM, classUnderTest.getPlatform());
    }
}
