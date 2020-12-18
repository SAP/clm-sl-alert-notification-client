package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConditionBuilderTest {

    private ConditionBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConditionBuilder();
    }

    @Test
    public void whenBuildIsCalled_thenCorrectConditionIsReturned() {
        Condition testCondition = classUnderTest //
                .withName(TEST_NAME) //
                .withDescription(TEST_DESCRIPTION) //
                .withPropertyKey(TEST_PROPERTY_KEY) //
                .withPredicate(TEST_PREDICATE) //
                .withPropertyValue(TEST_PROPERTY_VALUE) //
                .withMandatory(TEST_MANDATORY) //
                .withLabels(TEST_LABELS) //
                .build();

        Condition expectedCondition = new Condition( //
                TEST_NAME, //
                TEST_DESCRIPTION, //
                TEST_PROPERTY_KEY, //
                TEST_PREDICATE, //
                TEST_PROPERTY_VALUE, //
                TEST_MANDATORY, //
                TEST_LABELS //
        );

        assertEquals(expectedCondition, testCondition);
    }

    @Test
    public void givenThatNullNameIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withName(null) //
                .withDescription(TEST_DESCRIPTION) //
                .withPropertyKey(TEST_PROPERTY_KEY) //
                .withPredicate(TEST_PREDICATE) //
                .withPropertyValue(TEST_PROPERTY_VALUE) //
                .withMandatory(TEST_MANDATORY) //
                .withLabels(TEST_LABELS) //
                .build()
        );
    }

    @Test
    public void givenThatNullPropertyKeyIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withName(TEST_NAME) //
                .withDescription(TEST_DESCRIPTION) //
                .withPropertyKey(null) //
                .withPredicate(TEST_PREDICATE) //
                .withPropertyValue(TEST_PROPERTY_VALUE) //
                .withMandatory(TEST_MANDATORY) //
                .withLabels(TEST_LABELS) //
                .build()
        );
    }

    @Test
    public void givenThatNullPredicateIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withName(TEST_NAME) //
                .withDescription(TEST_DESCRIPTION) //
                .withPropertyKey(TEST_PROPERTY_KEY) //
                .withPredicate(null) //
                .withPropertyValue(TEST_PROPERTY_VALUE) //
                .withMandatory(TEST_MANDATORY) //
                .withLabels(TEST_LABELS) //
                .build()
        );
    }

}
