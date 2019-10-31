package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageMetadataTest {

    private static final Integer TEST_PAGE = Integer.valueOf(6);
    private static final Integer TEST_PAGE_SIZE = Integer.valueOf(100);
    private static final Long TEST_TOTAL_PAGES = Long.valueOf(20L);
    private static final Long TEST_TOTAL_RESULTS_COUNT = Long.valueOf(2000L);

    private PageMetadata classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new PageMetadata(TEST_PAGE, TEST_PAGE_SIZE, TEST_TOTAL_PAGES, TEST_TOTAL_RESULTS_COUNT);
    }

    @Test
    public void whenGetPageIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_PAGE, classUnderTest.getPage());
    }

    @Test
    public void whenGetPageSizeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_PAGE_SIZE, classUnderTest.getPageSize());
    }

    @Test
    public void whenGetTotalPagesIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TOTAL_PAGES, classUnderTest.getTotalPages());
    }

    @Test
    public void whenGetTotalResultsCountIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TOTAL_RESULTS_COUNT, classUnderTest.getTotalResultsCount());
    }
}
