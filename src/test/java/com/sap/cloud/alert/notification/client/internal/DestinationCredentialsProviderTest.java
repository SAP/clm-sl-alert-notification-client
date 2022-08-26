package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

public class DestinationCredentialsProviderTest {

    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final String TEST_DESTINATION_NAME = "TEST_DESTINATION_NAME";

    private static final String TEST_AUTHORIZATION_HEADER_VALUE = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
    private static final URI TEST_OAUTH_URI = URI.create("https://nowhere.near.a.real.uri.sap.com/oauth/token?grant_type=client_credentials");
    private static final DestinationContext TEST_DESTINATION_CONTEXT = new DestinationContext(TEST_SERVICE_REGION_URI, true);
    private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);

    private HttpClient mockedHttpClient;
    private IAuthorizationHeader mockedAuthorizationHeader;
    private DestinationServiceBinding destinationServiceBinding = new DestinationServiceBinding(URI.create(TEST_SERVICE_REGION_URI), TEST_OAUTH_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    private DestinationCredentialsProvider classUnderTest;

    @BeforeEach
    public void setUp() {
        mockedHttpClient = mock(HttpClient.class);
        mockedAuthorizationHeader = mock(IAuthorizationHeader.class);

        classUnderTest = new DestinationCredentialsProvider(
                TEST_DESTINATION_NAME,
                mockedHttpClient,
                mockedAuthorizationHeader,
                destinationServiceBinding
        );
    }

    @Test
    public void whenGetDestinationContextIsCalled_thenCorrectResultIsReturned() throws Exception{
        doReturn(createCertificateAuthenticationResponse()).when(mockedHttpClient).execute(any());

        assertEquals(classUnderTest.getDestinationContext(), TEST_DESTINATION_CONTEXT);

        verify(mockedHttpClient).execute(any(HttpUriRequest.class));
    }

    @Test
    public void whenIOExceptionOccurs_whenGetServiceBaseUriIsCalled_thenClientRequestExceptionIsThrown() throws Exception {
        doThrow(new IOException()).when(mockedHttpClient).execute(any());

        assertThrows( //
                ClientRequestException.class, //
                () -> classUnderTest.getDestinationContext() //
        );
    }

    @Test
    public void givenBasicAuthentication_whenGetAuthorizationHeaderIsCalled_thenCorrectResultIsReturned() throws Exception {
        doReturn(createBasicAuthenticationHeaderResponse()).when(mockedHttpClient).execute(any());

        IAuthorizationHeader result = classUnderTest.getAuthorizationHeader();

        assertEquals(result.getClass(), BasicAuthorizationHeader.class);
        assertEquals(result.getValue(), TEST_AUTHORIZATION_HEADER_VALUE);

    }

    @Test
    public void givenOauthAuthentication_whenGetAuthorizationHeaderIsCalled_thenCorrectResultIsReturned() throws Exception {
        doReturn(createOauthAuthenticationHeaderResponse()).when(mockedHttpClient).execute(any());

        IAuthorizationHeader result = classUnderTest.getAuthorizationHeader();

        assertCorrectOauthAuthorizationHeader(result);
    }

    @Test
    public void givenIOExceptionOccurs_whenGetAuthorizationHeaderIsCalled_thenClientRequestExceptionIsThrown() throws Exception {
        doThrow(new IOException()).when(mockedHttpClient).execute(any());

        assertThrows( //
                ClientRequestException.class, //
                () -> classUnderTest.getAuthorizationHeader() //
        );
    }

    @Test
    public void givenCertificateAuthentication_whenGetKeyStoreDetailsIsCalled_thenCorrectResultIsReturned() throws Exception {
        doReturn(createCertificateAuthenticationResponse()).when(mockedHttpClient).execute(any());

        assertEquals(classUnderTest.getKeyStoreDetails(), new KeyStoreDetails(KeyStoreType.PKCS12, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_P12));

        verify(mockedHttpClient).execute(any(HttpUriRequest.class));
    }

    @Test
    public void givenIOExceptionOccurs_whenGetKeyStoreDetailsIsCalled_thenClientRequestExceptionIsThrown() throws Exception {
        doThrow(new IOException()).when(mockedHttpClient).execute(any());

        assertThrows( //
                ClientRequestException.class, //
                () -> classUnderTest.getKeyStoreDetails() //
        );
    }

    private void assertCorrectOauthAuthorizationHeader(IAuthorizationHeader authorizationHeader) {
        BasicAuthorizationHeader basicAuthorizationHeader = extractFieldValue(authorizationHeader, "authorizationHeader");
        String oAuthToken = extractFieldValue(authorizationHeader, "currentOAuthToken");
        URI oAuthTokenUri = extractFieldValue(authorizationHeader, "oAuthTokenUri");

        assertNull(oAuthToken);
        assertEquals(oAuthTokenUri, TEST_OAUTH_URI);
        assertEquals(authorizationHeader.getClass(), OAuthAuthorizationHeader.class);
        assertEquals(basicAuthorizationHeader.getValue(), TEST_AUTHORIZATION_HEADER_VALUE);
    }

    private static HttpResponse createCertificateAuthenticationResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_CERTIFICATE_CREDENTIALS_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }

    private static HttpResponse createBasicAuthenticationHeaderResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_BASIC_AUTHORIZATION_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }

    private static HttpResponse createOauthAuthenticationHeaderResponse() {
        BasicHttpEntity entity = new BasicHttpEntity();
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, SC_OK, null));

        response.setEntity(entity);
        entity.setContent(new ByteArrayInputStream(HTTP_OAUTH_AUTHORIZATION_RESPONSE_FORMAT.getBytes(UTF_8)));

        return response;
    }
}
