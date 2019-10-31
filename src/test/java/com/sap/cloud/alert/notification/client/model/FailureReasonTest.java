package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailureReasonTest {

    private static final Long TEST_TIMESTAMP = Long.valueOf(1L);
    private static final String TEST_REASON = "Not Found";
    private static final Integer TEST_CODE = Integer.valueOf(404);

    private FailureReason classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new FailureReason(TEST_CODE, TEST_REASON, TEST_TIMESTAMP);
    }

    @Test
    public void whenGetCodeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_CODE, classUnderTest.getCode());
    }

    @Test
    public void whenGetReasonIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_REASON, classUnderTest.getReason());
    }

    @Test
    public void whenGetTimestampIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TIMESTAMP, classUnderTest.getTimestamp());
    }
}
