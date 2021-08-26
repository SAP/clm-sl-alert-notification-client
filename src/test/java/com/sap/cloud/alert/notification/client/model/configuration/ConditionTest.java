package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.lang.String.format;
import static org.apache.commons.collections4.SetUtils.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionTest {

    private static final String TEST_CONDITION_AS_JSON_STRING = "{"
            + format("\"id\":\"%s\",", TEST_ID)
            + format("\"name\":\"%s\",", TEST_NAME)
            + format("\"description\":\"%s\",", TEST_DESCRIPTION)
            + format("\"propertyKey\":\"%s\",", TEST_PROPERTY_KEY)
            + format("\"predicate\":\"%s\",", TEST_PREDICATE.name())
            + format("\"propertyValue\":\"%s\",", TEST_PROPERTY_VALUE)
            + format("\"mandatory\":%s,", TEST_MANDATORY)
            + format("\"labels\":%s,", toJsonString(TEST_LABELS))
            + format("\"timeCreated\":%d,", TEST_TIME_CREATED)
            + format("\"lastModified\":%d", TEST_LAST_MODIFIED)
            + "}";

    private Condition classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new Condition(
                TEST_ID,
                TEST_NAME,
                TEST_DESCRIPTION,
                TEST_PROPERTY_KEY,
                TEST_PREDICATE,
                TEST_PROPERTY_VALUE,
                TEST_MANDATORY,
                TEST_LABELS,
                TEST_TIME_CREATED,
                TEST_LAST_MODIFIED
        );
    }

    @Test
    public void whenConditionIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEqualJsonStrings(TEST_CONDITION_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void whenConditionIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_CONDITION_AS_JSON_STRING, Condition.class));
    }

    @Test
    public void whenGetIdIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_ID, classUnderTest.getId());
    }

    @Test
    public void whenGetNameIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_NAME, classUnderTest.getName());
    }

    @Test
    public void whenGetDescriptionIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_DESCRIPTION, classUnderTest.getDescription());
    }

    @Test
    public void whenGetPropertyKeyIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PROPERTY_KEY, classUnderTest.getPropertyKey());
    }

    @Test
    public void whenGetPredicateIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PREDICATE, classUnderTest.getPredicate());
    }

    @Test
    public void whenGetPropertyValueIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PROPERTY_VALUE, classUnderTest.getPropertyValue());
    }

    @Test
    public void whenGetMandatoryIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_MANDATORY, classUnderTest.getMandatory());
    }

    @Test
    public void whenGetLabelsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_LABELS, classUnderTest.getLabels());
    }

    @Test
    public void whenGetTimeCreatedIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_TIME_CREATED, classUnderTest.getTimeCreated());
    }

    @Test
    public void whenGetLastModifiedIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_LAST_MODIFIED, classUnderTest.getLastModified());
    }

    @Test
    public void givenThatLabelsAreNull_whenConstructorIsInvoked_thenLabelsAreSetToAnEmptyCollection() {
        classUnderTest = new Condition(
                TEST_ID,
                TEST_NAME,
                TEST_DESCRIPTION,
                TEST_PROPERTY_KEY,
                TEST_PREDICATE,
                TEST_PROPERTY_VALUE,
                TEST_MANDATORY,
                null,
                TEST_TIME_CREATED,
                TEST_LAST_MODIFIED
        );

        assertEquals(emptySet(), classUnderTest.getLabels());
    }
}
