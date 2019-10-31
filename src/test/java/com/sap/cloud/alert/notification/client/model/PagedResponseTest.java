package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagedResponseTest {

    private static final Collection<ConsumerEvent> TEST_RESULTS = Collections.emptyList();
    private static final PageMetadata TEST_PAGE_METADATA = new PageMetadata(0, 0, 0L, 0L);

    private PagedResponse classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new PagedResponse(TEST_PAGE_METADATA, TEST_RESULTS);
    }

    @Test
    public void whenGetMetadataIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_PAGE_METADATA, classUnderTest.getMetadata());
    }

    @Test
    public void whenGetResultsIsCalled_thenCorrectResultIsReturned() {
        assertEquals(new ArrayList<>(TEST_RESULTS), new ArrayList<>(classUnderTest.getResults()));
    }
}
