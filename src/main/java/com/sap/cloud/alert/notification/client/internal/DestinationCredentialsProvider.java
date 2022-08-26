package com.sap.cloud.alert.notification.client.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.assertHttpStatus;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.buildDestinationServiceURI;
import static com.sap.cloud.alert.notification.client.internal.KeyStoreType.*;
import static java.util.Arrays.asList;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpStatus.SC_OK;

public class DestinationCredentialsProvider {

    private static final String DESTINATION_SERVICE_FAILURE = "Failed to read destination";
    private static final String UNSUPPORTED_AUTHENTICATION_TYPE = "Authentication type configured in destination '%s' is not supported";

    private static final String BASIC_AUTHENTICATION_TYPE = "BasicAuthentication";
    private static final String OAUTH_AUTHENTICATION_TYPE = "OAuth2ClientCredentials";
    private static final String CERTIFICATE_AUTHENTICATION_TYPE = "ClientCertificateAuthentication";

    private static final String DESTINATION_CONFIGURATION_KEY = "destinationConfiguration";
    private static final String USERNAME_KEY = "User";
    private static final String PASSWORD_KEY = "Password";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";
    private static final String OAUTH_URI_KEY = "tokenServiceURL";

    private static final String AUTHENTICATION_KEY = "Authentication";
    private static final String DESTINATION_URL_KEY = "URL";
    private static final String CERTIFICATE_NAME_KEY = "Name";
    private static final String CERTIFICATE_CONTENT_KEY = "Content";
    private static final String KEYSTORE_PASSWORD_KEY = "KeyStorePassword";
    private static final String CERTIFICATE_KEY =  "certificates";

    private static final String JKS_EXTENSION = "jks";
    private static final String PKCS12_EXTENSION = "p12";
    private static final String PFX_EXTENSION = "pfx";

    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    private static final List<String> ALLOWED_AUTHENTICATION_TYPES = asList(BASIC_AUTHENTICATION_TYPE, OAUTH_AUTHENTICATION_TYPE, CERTIFICATE_AUTHENTICATION_TYPE);

    private final URI destinationUri;
    private final HttpClient httpClient;
    private final IAuthorizationHeader authorizationHeader;

    public DestinationCredentialsProvider( //
               String destinationName,
               HttpClient httpClient,
               IAuthorizationHeader authorizationHeader,
               DestinationServiceBinding destinationServiceBinding
    ) {
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
        this.destinationUri = buildDestinationServiceURI(destinationServiceBinding.getServiceUri(), destinationName);
    }

    public DestinationContext getDestinationContext() {
        try {
            HttpResponse response = httpClient.execute(buildRequest());

            assertHttpStatus(response, SC_OK);

            return buildDestinationContext(response.getEntity());
        } catch (Exception e) {
            throw new ClientRequestException(DESTINATION_SERVICE_FAILURE, e);
        }
    }

    public IAuthorizationHeader getAuthorizationHeader() {
        try {
            HttpResponse response = httpClient.execute(buildRequest());

            assertHttpStatus(response, SC_OK);

            return createAuthorizationHeader(response.getEntity());
        } catch (Exception e) {
            throw new ClientRequestException(DESTINATION_SERVICE_FAILURE, e);
        }
    }

    public KeyStoreDetails getKeyStoreDetails() {
        try {
            HttpResponse response = httpClient.execute(buildRequest());

            assertHttpStatus(response, SC_OK);

            return buildKeystoreDetails(response.getEntity());
        } catch (Exception e) {
            throw new ClientRequestException(DESTINATION_SERVICE_FAILURE, e);
        }
    }

    private HttpGet buildRequest() {
        HttpGet request = new HttpGet(destinationUri);

        request.setHeader(AUTHORIZATION, authorizationHeader.getValue());

        return request;
    }

    private DestinationContext buildDestinationContext(HttpEntity response) throws IOException {
        try (InputStream responseBodyAsStream = response.getContent()) {
            JsonNode responseBody = JSON_OBJECT_MAPPER.readTree(responseBodyAsStream);

            JsonNode destinationConfiguration = responseBody.get(DESTINATION_CONFIGURATION_KEY);

            String serviceUri = destinationConfiguration.get(DESTINATION_URL_KEY).asText();
            String authenticationType = destinationConfiguration.get(AUTHENTICATION_KEY).asText();
            assertValidAuthenticationType(authenticationType);


            return new DestinationContext(serviceUri, authenticationType.equals(CERTIFICATE_AUTHENTICATION_TYPE));
        }
    }

    private IAuthorizationHeader createAuthorizationHeader(HttpEntity response) throws IOException {
        try (InputStream responseBodyAsStream = response.getContent()) {
            JsonNode responseBody = JSON_OBJECT_MAPPER.readTree(responseBodyAsStream);

            JsonNode destinationConfiguration = responseBody.get(DESTINATION_CONFIGURATION_KEY);
            String authenticationType = destinationConfiguration.get(AUTHENTICATION_KEY).asText();

            assertValidAuthenticationType(authenticationType);

            return authenticationType.equals(BASIC_AUTHENTICATION_TYPE) ? builtBasicAuthorizationHeader(destinationConfiguration)
                    : builtOauthAuthorizationHeader(destinationConfiguration);
        }
    }

    private IAuthorizationHeader builtBasicAuthorizationHeader(JsonNode destinationConfiguration) {
        return new BasicAuthorizationHeader( //
                destinationConfiguration.get(USERNAME_KEY).asText(), //
                destinationConfiguration.get(PASSWORD_KEY).asText() //
        );
    }

    private IAuthorizationHeader builtOauthAuthorizationHeader(JsonNode destinationConfiguration) {
        return new OAuthAuthorizationHeader( //
                destinationConfiguration.get(CLIENT_ID_KEY).asText(), //
                destinationConfiguration.get(CLIENT_SECRET_KEY).asText(),  //
                URI.create(destinationConfiguration.get(OAUTH_URI_KEY).asText()), //
                httpClient //
        );
    }

    private void assertValidAuthenticationType(String authenticationType) {
        if (!ALLOWED_AUTHENTICATION_TYPES.contains(authenticationType)) {
            throw new ClientRequestException(String.format(UNSUPPORTED_AUTHENTICATION_TYPE, authenticationType));
        }
    }

    private KeyStoreDetails buildKeystoreDetails(HttpEntity response) throws IOException{
        try (InputStream responseBodyAsStream = response.getContent()) {
            JsonNode responseBody = JSON_OBJECT_MAPPER.readTree(responseBodyAsStream);

            JsonNode certificate = responseBody.get(CERTIFICATE_KEY).get(0);

            String keyStoreContent = certificate.get(CERTIFICATE_CONTENT_KEY).asText();
            String certificateName = certificate.get(CERTIFICATE_NAME_KEY).asText();
            String keystorePassword = responseBody.get(DESTINATION_CONFIGURATION_KEY).get(KEYSTORE_PASSWORD_KEY).asText();

            return new KeyStoreDetails( //
                    getFromCertificateName(certificateName), //
                    keystorePassword, //
                    keyStoreContent //
            );
        }
    }

    private KeyStoreType getFromCertificateName(String certificateName){
        return certificateName.endsWith(JKS_EXTENSION) ? JKS :
                certificateName.endsWith(PKCS12_EXTENSION) ? PKCS12 : certificateName.endsWith(PFX_EXTENSION) ?  PFX: PEM;
    }
}
