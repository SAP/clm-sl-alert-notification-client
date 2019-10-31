package com.sap.cloud.alert.notification.client.model;

import com.sap.cloud.alert.notification.client.internal.AlertNotificationAsyncClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerResourceEventTest {

    private static final String TEST_ID = "TEST_EVENT_ID";
    private static final String TEST_BODY = "TEST_BODY";
    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final Long TEST_TIMESTAMP = Instant.now().getEpochSecond();
    private static final Integer TEST_PRIORITY = Integer.valueOf(123);
    private static final EventSeverity TEST_SEVERITY = EventSeverity.INFO;
    private static final EventCategory TEST_CATEGORY = EventCategory.NOTIFICATION;
    private static final Map<String, String> TEST_TAGS = singletonMap("test_key", "test_value");
    private static final AffectedCustomerResource TEST_AFFECTED_RESOURCE = new AffectedCustomerResource("test_name", "test_type", "test_instance", emptyMap());


    private CustomerResourceEvent classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new CustomerResourceEvent(
                TEST_ID,
                TEST_TYPE,
                TEST_TIMESTAMP,
                TEST_SEVERITY,
                TEST_CATEGORY,
                TEST_PRIORITY,
                TEST_SUBJECT,
                TEST_BODY,
                TEST_TAGS,
                TEST_AFFECTED_RESOURCE
        );
    }

    @Test
    public void givenTypeIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, null, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenBodyIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, null, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenSubjectIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, TEST_PRIORITY, null, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenSeverityIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, null, TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenCategoryIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, null, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenResourceIsNull_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, null);
        });
    }

    @Test
    public void givenPriorityIsNotValid_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, -1, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, 0, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, 1001, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenCategoryIsNotValid_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, EventCategory.valueOf("unknown-category"), TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
    }

    @Test
    public void givenSeverityIsNotValid_whenConstructingEvent_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerResourceEvent(TEST_ID, TEST_TYPE, TEST_TIMESTAMP, EventSeverity.valueOf("unknown-severity"), TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        });
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
    public void whenGetTagsIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_TAGS, classUnderTest.getTags());
    }

    @Test
    public void whenGetResourceIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_AFFECTED_RESOURCE, classUnderTest.getResource());
    }
}
