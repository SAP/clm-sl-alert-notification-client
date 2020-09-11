package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagingMetadataTest {

    private static final String TEST_PAGING_METADATA_AS_JSON_STRING = "{" 
            + String.format("\"page\":%d,", TEST_PAGE) 
            + String.format("\"pageSize\":%d,", TEST_PAGE_SIZE) 
            + String.format("\"totalCount\":%d,", TEST_TOTAL_RESULTS_COUNT) 
            + String.format("\"totalPages\":%d", TEST_TOTAL_PAGES) 
            + "}";

    private PagingMetadata classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new PagingMetadata(TEST_PAGE, TEST_PAGE_SIZE, TEST_TOTAL_PAGES, TEST_TOTAL_RESULTS_COUNT);
    }

    @Test
    public void whenPagingMetadataIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_PAGING_METADATA_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void whenPagingMetadataIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_PAGING_METADATA_AS_JSON_STRING, PagingMetadata.class));
    }

    @Test
    public void whenGetPageIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PAGE, classUnderTest.getPage());
    }

    @Test
    public void whenGetPageSize_thenCorrectValueIsReturned() {
        assertEquals(TEST_PAGE_SIZE, classUnderTest.getPageSize());
    }

    @Test
    public void whenGetTotalPages_thenCorrectValueIsReturned() {
        assertEquals(TEST_TOTAL_PAGES, classUnderTest.getTotalPages());
    }

    @Test
    public void whenGetTotalResultsCount_thenCorrectValueIsReturned() {
        assertEquals(TEST_TOTAL_RESULTS_COUNT, classUnderTest.getTotalCount());
    }
}
