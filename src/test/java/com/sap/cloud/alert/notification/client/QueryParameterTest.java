package com.sap.cloud.alert.notification.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryParameterTest {

    private static final String TEST_KEY = "test";

    private QueryParameter classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new QueryParameter(TEST_KEY);
    }

    @Test
    public void whenGetKeyIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_KEY, classUnderTest.getKey());
    }
}
