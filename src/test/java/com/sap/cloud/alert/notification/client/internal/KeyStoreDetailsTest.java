package com.sap.cloud.alert.notification.client.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyStoreDetailsTest {
  
    private static final String TEST_KEYSTORE_CONTENT = "TEST_KEYSTORE_CONTENT";
    private static final String TEST_KEYSTORE_PASSWORD = "TEST_KEYSTORE_PASSWORD";
    private static final KeyStoreType TEST_KEYSTORE_TYPE = KeyStoreType.JKS;

    private KeyStoreDetails classUnderTest;

    @BeforeEach
    public void setUp(){
        classUnderTest = new KeyStoreDetails(TEST_KEYSTORE_TYPE, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT);
    }

    @Test
    public void whenGetKeyStoreContentIsCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.getKeyStoreContent(), TEST_KEYSTORE_CONTENT);
    }

    @Test
    public void whenGetKeyStorePasswordIsCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.getKeyStorePassword(), TEST_KEYSTORE_PASSWORD);
    }

    @Test
    public void whenGetKeyStoreTypeTypeIsCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.getKeyStoreType(), KeyStoreType.JKS);
    }
}
