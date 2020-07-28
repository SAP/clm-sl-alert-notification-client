package com.sap.cloud.alert.notification.client.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SimpleRetryPolicyTest {

    private static final int TEST_RETRIES = 3;
    private static final String TEST_RESULT = "TEST_RESULT";
    private static final Duration TEST_BACKOFF = Duration.ofMillis(100);

    private SimpleRetryPolicy classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new SimpleRetryPolicy(TEST_RETRIES, TEST_BACKOFF);
    }

    @Test
    public void whenGetMaxRetriesIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getMaxRetries(), TEST_RETRIES);
    }

    @Test
    public void whenGetRetryBackoffIsCalled_thenCorrectValueIsReturned() {
        assertEquals(classUnderTest.getRetryBackoff(), TEST_BACKOFF);
    }

    @Test
    public void givenThatNoArgConstructorIsUsed_whenInstanceIsCreated_thenCorrectInstanceIsCreated() {
        classUnderTest = new SimpleRetryPolicy();

        assertEquals(classUnderTest.getMaxRetries(), 0);
        assertEquals(classUnderTest.getRetryBackoff(), Duration.ZERO);
    }

    @Test
    public void givenThatExecutionFails_whenExecuteWithRetryIsCalled_thenExecutionIsRetried_and_correctResultIsReturned() {
        for (int intendedFailures = 0; intendedFailures < TEST_RETRIES; ++intendedFailures) {
            Supplier<String> testSupplier = createMockedSupplier(intendedFailures);

            assertEquals(classUnderTest.executeWithRetry(testSupplier), TEST_RESULT);

            verify(testSupplier, times(intendedFailures + 1)).get();
        }
    }

    @Test
    public void givenThatExecutionStillFailsAfterAllRetries_whenExecuteWithRetryIsCalled_thenExceptionIsThrown() {
        assertThrows(RuntimeException.class, () -> {
            Supplier<String> testSupplier = createMockedSupplier(TEST_RETRIES + 1);

            try {
                classUnderTest.executeWithRetry(testSupplier);
            } finally {
                verify(testSupplier, times(TEST_RETRIES + 1)).get();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String> createMockedSupplier(int failures) {
        Supplier<String> testSupplier = mock(Supplier.class);
        OngoingStubbing<String> ongoingStubbing = when(testSupplier.get());

        for (int i = 0; i < failures; ++i) {
            ongoingStubbing = ongoingStubbing.thenThrow(RuntimeException.class);
        }

        ongoingStubbing.thenReturn(TEST_RESULT);

        return testSupplier;
    }
}
