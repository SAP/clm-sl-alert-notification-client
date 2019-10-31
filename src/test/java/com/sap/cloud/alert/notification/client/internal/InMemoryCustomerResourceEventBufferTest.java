package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.BufferOverflowException;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryCustomerResourceEventBufferTest {

    private static final int TEST_CAPACITY = 10;

    private CustomerResourceEvent testCustomerResourceEvent;
    private InMemoryCustomerResourceEventBuffer classUnderTest;

    @BeforeEach
    public void setUp() {
        testCustomerResourceEvent = Mockito.mock(CustomerResourceEvent.class);
        classUnderTest = new InMemoryCustomerResourceEventBuffer(TEST_CAPACITY);
    }

    @Test
    public void whenGetCapacityIsCalled_thenCorrectResultIsReturned() {
        assertEquals(TEST_CAPACITY, classUnderTest.getCapacity());
    }

    @Test
    public void whenWriteIsCalled_thenCorrectValueIsWrittenInBuffer() {
        UUID recordUUID = classUnderTest.write(testCustomerResourceEvent);

        assertSame(testCustomerResourceEvent, classUnderTest.read(recordUUID));
    }

    @Test
    public void givenThatBufferIsFull_whenWriteIsCalled_thenExceptionIsThrown() {
        for (int i = 0; i < TEST_CAPACITY; ++i) {
            classUnderTest.write(testCustomerResourceEvent);
        }

        assertThrows(BufferOverflowException.class, () -> {
            classUnderTest.write(testCustomerResourceEvent);
        });
    }

    @Test
    public void givenThatEventIsReadFromBuffer_whenReadIsCalled_thenEventIsRemovedFromBuffer() {
        UUID recordUUID = classUnderTest.write(testCustomerResourceEvent);

        assertSame(testCustomerResourceEvent, classUnderTest.read(recordUUID));
        assertNull(classUnderTest.read(recordUUID));
    }
}
