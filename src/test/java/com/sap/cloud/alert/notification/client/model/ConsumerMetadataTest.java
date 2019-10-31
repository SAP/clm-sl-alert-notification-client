package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsumerMetadataTest {

    private static final Long TEST_CACHE_TIME = Long.valueOf(20L);
    private static final DeliveryStatus TEST_DELIVERY_STATUS = DeliveryStatus.MATCHED;
    private static final String TEST_AFFECTED_ACTION_ID = UUID.randomUUID().toString();
    private static final Collection<FailureReason> TEST_FAILURE_REASONS = Collections.singletonList(new FailureReason(Integer.valueOf(500), "Internal Server Error", Long.valueOf(0L)));

    private ConsumerMetadata classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConsumerMetadata(TEST_CACHE_TIME, TEST_DELIVERY_STATUS, TEST_AFFECTED_ACTION_ID, TEST_FAILURE_REASONS);
    }

    @Test
    public void whenGetCacheTimeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_CACHE_TIME, classUnderTest.getCacheTime());
    }

    @Test
    public void whenGetAffectedActionIdIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_AFFECTED_ACTION_ID, classUnderTest.getAffectedActionId());
    }

    @Test
    public void whenGetDeliveryStatusIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_DELIVERY_STATUS, classUnderTest.getDeliveryStatus());
    }

    @Test
    public void whenGetFailureReasonsIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_FAILURE_REASONS, classUnderTest.getFailureReasons());
    }
}
