package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Duration;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OAuthAuthorizationHeaderTest {

    private static final String TEST_PROTOCOL_NAME = "HTTP";
    private static final int TEST_PROTOCOL_MAJOR_VERSION = 3;
    private static final int TEST_PROTOCOL_MINOR_VERSION = 0;
    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_REASON_PHRASE = "Test Reason";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final String TEST_ACCESS_TOKEN_1 = "TEST_ACCESS_TOKEN_1";
    private static final String TEST_ACCESS_TOKEN_2 = "TEST_ACCESS_TOKEN_2";
    private static final URI TEST_UAA_URI = URI.create("https://no.such.url.sap.com/oauth/token?grant_type=client_credentials");
    private static final ProtocolVersion TEST_PROTOCOL_VERSION = new ProtocolVersion(TEST_PROTOCOL_NAME, TEST_PROTOCOL_MAJOR_VERSION, TEST_PROTOCOL_MINOR_VERSION);


    private HttpClient mockedHttpClient;
    private OAuthAuthorizationHeader classUnderTest;

    @BeforeEach
    public void setUp() {
        mockedHttpClient = Mockito.mock(HttpClient.class);
        classUnderTest = new OAuthAuthorizationHeader(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_UAA_URI, mockedHttpClient);
    }

    @Test
    public void givenThatNoCachedTokenIsAvailable_whenGetValueIsCalled_thenFreshTokenIsObtained() throws IOException {
        when(mockedHttpClient.execute(any(HttpPost.class))).thenReturn(createMockedUAATokenResponse(TEST_ACCESS_TOKEN_1, Duration.ofMinutes(5)));

        assertEquals(String.format("Bearer %s", TEST_ACCESS_TOKEN_1), classUnderTest.getValue());

        verify(mockedHttpClient, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void givenThatCachedTokenIsAvailable_and_TokenIsStillValid_whenGetValueIsCalled_thenCashedTokenIsReturned() throws IOException {
        when(mockedHttpClient.execute(any(HttpPost.class)))
                .thenReturn(createMockedUAATokenResponse(TEST_ACCESS_TOKEN_1, Duration.ofHours(1)))
                .thenReturn(createMockedUAATokenResponse(TEST_ACCESS_TOKEN_2, Duration.ofHours(1)));

        assertEquals(String.format("Bearer %s", TEST_ACCESS_TOKEN_1), classUnderTest.getValue());
        assertEquals(String.format("Bearer %s", TEST_ACCESS_TOKEN_1), classUnderTest.getValue());

        verify(mockedHttpClient, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void givenThatCachedTokenIsAvailable_and_TokenIsExpired_whenGetValueIsCalled_thenCashedTokenIsReturned() throws IOException {
        when(mockedHttpClient.execute(any(HttpPost.class)))
                .thenReturn(createMockedUAATokenResponse(TEST_ACCESS_TOKEN_1, Duration.ofSeconds(1)))
                .thenReturn(createMockedUAATokenResponse(TEST_ACCESS_TOKEN_2, Duration.ofMinutes(5)));

        assertEquals(String.format("Bearer %s", TEST_ACCESS_TOKEN_1), classUnderTest.getValue());
        assertEquals(String.format("Bearer %s", TEST_ACCESS_TOKEN_2), classUnderTest.getValue());

        verify(mockedHttpClient, times(2)).execute(any(HttpPost.class));
    }

    @Test
    public void givenThatUAARequestFails_whenGetValueIsCalled_thenExceptionIsThrown() throws IOException {
        doThrow(new IOException()).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ClientRequestException.class, () -> classUnderTest.getValue());
    }

    private HttpResponse createMockedUAATokenResponse(String accessToken, Duration expireAfter) throws UnsupportedEncodingException {
        StringEntity responseEntity = new StringEntity(String.format("{ \"access_token\": \"%s\", \"expires_in\": %d }", accessToken, expireAfter.getSeconds()));
        StatusLine statusline = new BasicStatusLine(TEST_PROTOCOL_VERSION, SC_OK, TEST_REASON_PHRASE);
        HttpResponse httpResponse = new BasicHttpResponse(statusline);

        httpResponse.setEntity(responseEntity);

        return httpResponse;
    }
}
