package com.sap.cloud.alert.notification.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsumerEventTest {

    private static final String TEST_ID = "TEST_EVENT_ID";
    private static final String TEST_BODY = "TEST_BODY";
    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_REGION = "TEST_REGION";
    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final Long TEST_TIMESTAMP = Instant.now().getEpochSecond();
    private static final Integer TEST_PRIORITY = Integer.valueOf(123);
    private static final String TEST_REGION_TYPE = "TEST_REGION_TYPE";
    private static final EventSeverity TEST_SEVERITY = EventSeverity.INFO;
    private static final EventCategory TEST_CATEGORY = EventCategory.NOTIFICATION;
    private static final Map<String, String> TEST_TAGS = singletonMap("test_key", "test_value");
    private static final Collection<FailureReason> TEST_FAILURE_REASONS = Collections.singletonList(new FailureReason(Integer.valueOf(500), "Internal Server Error", Long.valueOf(0L)));
    private static final ConsumerMetadata TEST_METADATA = new ConsumerMetadata(12345L, DeliveryStatus.UNDELIVERED, "test_action_id", TEST_FAILURE_REASONS);
    private static final AffectedCustomerResource TEST_AFFECTED_RESOURCE = new AffectedCustomerResource("test_name", "test_type", "test_instance", emptyMap());


    private ConsumerEvent classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConsumerEvent(
                TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY,
                TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY,
                TEST_TAGS, TEST_AFFECTED_RESOURCE, TEST_REGION,
                TEST_REGION_TYPE, TEST_METADATA
        );
    }

    @Test
    public void whenGetIdIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_ID, classUnderTest.getId());
    }

    @Test
    public void whenGetEventTypeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TYPE, classUnderTest.getEventType());
    }

    @Test
    public void whenGetEventTimestampIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TIMESTAMP, classUnderTest.getEventTimestamp());
    }

    @Test
    public void whenGetSeverityIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_SEVERITY, classUnderTest.getSeverity());
    }

    @Test
    public void whenGetCategoryIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_CATEGORY, classUnderTest.getCategory());
    }

    @Test
    public void whenGetSubjectIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_SUBJECT, classUnderTest.getSubject());
    }

    @Test
    public void whenGetBodyIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_BODY, classUnderTest.getBody());
    }

    @Test
    public void whenGetPriorityIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_PRIORITY, classUnderTest.getPriority());
    }

    @Test
    public void whenGetRegionIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_REGION, classUnderTest.getRegion());
    }

    @Test
    public void whenGetRegionTypeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_REGION_TYPE, classUnderTest.getRegionType());
    }

    @Test
    public void whenGetMetadataIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_METADATA, classUnderTest.getMetadata());
    }

    @Test
    public void whenGetTagsIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TAGS, classUnderTest.getTags());
    }

    @Test
    public void whenGetResourceIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_AFFECTED_RESOURCE, classUnderTest.getResource());
    }
}
