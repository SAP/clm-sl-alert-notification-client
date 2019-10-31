package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.AffectedCustomerResource;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.EventCategory;
import com.sap.cloud.alert.notification.client.model.EventSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerResourceEventBuilderTest {

    private static final String TEST_BODY = "TEST_BODY";
    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_SUBJECT = "TEST_SUBJECT";
    private static final Long TEST_TIMESTAMP = Instant.now().getEpochSecond();
    private static final Integer TEST_PRIORITY = Integer.valueOf(123);
    private static final EventSeverity TEST_SEVERITY = EventSeverity.INFO;
    private static final EventCategory TEST_CATEGORY = EventCategory.NOTIFICATION;
    private static final Map<String, String> TEST_TAGS = singletonMap("test_key", "test_value");
    private static final AffectedCustomerResource TEST_AFFECTED_RESOURCE = new AffectedCustomerResource("test_name", "test_type", "test_instance", emptyMap());

    private CustomerResourceEventBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new CustomerResourceEventBuilder();
    }

    @Test
    public void whenBuilderIsUsed_thenCorrectAffectedResourceIsBuild() {
        CustomerResourceEvent expectedEvent = new CustomerResourceEvent(null, TEST_TYPE, TEST_TIMESTAMP, TEST_SEVERITY, TEST_CATEGORY, TEST_PRIORITY, TEST_SUBJECT, TEST_BODY, TEST_TAGS, TEST_AFFECTED_RESOURCE);
        CustomerResourceEvent createdEvent = classUnderTest
                .withBody(TEST_BODY)
                .withTags(TEST_TAGS)
                .withType(TEST_TYPE)
                .withSubject(TEST_SUBJECT)
                .withSeverity(TEST_SEVERITY)
                .withCategory(TEST_CATEGORY)
                .withPriority(TEST_PRIORITY)
                .withEventTimestamp(TEST_TIMESTAMP)
                .withAffectedResource(TEST_AFFECTED_RESOURCE)
                .build();

        assertEquals(expectedEvent.getId(), createdEvent.getId());
        assertEquals(expectedEvent.getBody(), createdEvent.getBody());
        assertEquals(expectedEvent.getTags(), createdEvent.getTags());
        assertEquals(expectedEvent.getSubject(), createdEvent.getSubject());
        assertEquals(expectedEvent.getSeverity(), createdEvent.getSeverity());
        assertEquals(expectedEvent.getCategory(), createdEvent.getCategory());
        assertEquals(expectedEvent.getPriority(), createdEvent.getPriority());
        assertEquals(expectedEvent.getResource(), createdEvent.getResource());
        assertEquals(expectedEvent.getEventType(), createdEvent.getEventType());
        assertEquals(expectedEvent.getEventTimestamp(), createdEvent.getEventTimestamp());
    }

    @Test
    public void givenThatTypeIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withType(null)
                    .withBody(TEST_BODY)
                    .withTags(TEST_TAGS)
                    .withSubject(TEST_SUBJECT)
                    .withSeverity(TEST_SEVERITY)
                    .withCategory(TEST_CATEGORY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .withAffectedResource(TEST_AFFECTED_RESOURCE)
                    .build();
        });
    }

    @Test
    public void givenThatSeverityIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withSeverity(null)
                    .withType(TEST_TYPE)
                    .withBody(TEST_BODY)
                    .withTags(TEST_TAGS)
                    .withSubject(TEST_SUBJECT)
                    .withCategory(TEST_CATEGORY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .withAffectedResource(TEST_AFFECTED_RESOURCE)
                    .build();
        });
    }

    @Test
    public void givenThatCategoryIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withCategory(null)
                    .withType(TEST_TYPE)
                    .withBody(TEST_BODY)
                    .withTags(TEST_TAGS)
                    .withSubject(TEST_SUBJECT)
                    .withSeverity(TEST_SEVERITY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .withAffectedResource(TEST_AFFECTED_RESOURCE)
                    .build();
        });
    }

    @Test
    public void givenThatSubjectIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withSubject(null)
                    .withType(TEST_TYPE)
                    .withBody(TEST_BODY)
                    .withTags(TEST_TAGS)
                    .withSeverity(TEST_SEVERITY)
                    .withCategory(TEST_CATEGORY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .withAffectedResource(TEST_AFFECTED_RESOURCE)
                    .build();
        });
    }

    @Test
    public void givenThatBodyIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withBody(null)
                    .withType(TEST_TYPE)
                    .withTags(TEST_TAGS)
                    .withSubject(TEST_SUBJECT)
                    .withSeverity(TEST_SEVERITY)
                    .withCategory(TEST_CATEGORY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .withAffectedResource(TEST_AFFECTED_RESOURCE)
                    .build();
        });
    }

    @Test
    public void givenThatAffectedResourceIsNotGiven_whenBuilderIsUsed_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest
                    .withAffectedResource(null)
                    .withBody(TEST_BODY)
                    .withType(TEST_TYPE)
                    .withTags(TEST_TAGS)
                    .withSubject(TEST_SUBJECT)
                    .withSeverity(TEST_SEVERITY)
                    .withCategory(TEST_CATEGORY)
                    .withPriority(TEST_PRIORITY)
                    .withEventTimestamp(TEST_TIMESTAMP)
                    .build();
        });
    }
}
