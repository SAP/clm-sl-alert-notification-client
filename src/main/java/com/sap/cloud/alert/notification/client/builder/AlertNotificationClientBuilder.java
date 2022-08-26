package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.Platform;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.internal.*;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static com.sap.cloud.alert.notification.client.Platform.CF;
import static java.util.Objects.*;

public class AlertNotificationClientBuilder {

    private String username;
    private String password;
    private URI oAuthServiceUri;
    private HttpClient httpClient;
    private IRetryPolicy retryPolicy;
    private ServiceRegion serviceRegion;
    private IAuthorizationHeader authorizationHeader;
    private DestinationCredentialsProvider destinationCredentialsProvider;
    private boolean isCertificateAuthentication = false;
    private KeyStoreDetails keyStoreDetails;
    private Long invalidationTime;

    public AlertNotificationClientBuilder(HttpClient httpClient) {
        this.serviceRegion = null;
        this.authorizationHeader = null;
        this.httpClient = requireNonNull(httpClient);
        this.retryPolicy = new SimpleRetryPolicy();
    }

    public AlertNotificationClientBuilder withRetryPolicy(IRetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;

        return this;
    }

    public AlertNotificationClientBuilder withServiceRegion(ServiceRegion serviceRegion) {
        this.serviceRegion = serviceRegion;

        return this;
    }

    public AlertNotificationClientBuilder withAuthentication(String clientId, String clientSecret) {
        this.username = clientId;
        this.password = clientSecret;
        return this;
    }

    public AlertNotificationClientBuilder withAuthentication(String clientId, String clientSecret, URI oAuthServiceURI) {
        this.username = clientId;
        this.password = clientSecret;
        this.oAuthServiceUri = oAuthServiceURI;
        return this;
    }

    public AlertNotificationClient buildFromServiceBinding() {
        return buildFromServiceBinding(new AlertNotificationServiceBinding());
    }

    public AlertNotificationClient buildFromServiceBinding(AlertNotificationServiceBinding serviceBinding) {
        AlertNotificationClientBuilder alertNotificationClientBuilder = withServiceRegion(new ServiceRegion(CF, serviceBinding.getServiceUri().toString()));

        return isNull(serviceBinding.getOauthUri()) //
                ? alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret()).build() //
                : alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret(), serviceBinding.getOauthUri()).build();
    }

    public AlertNotificationClient buildFromDestinationBinding(DestinationServiceBinding destinationServiceBinding, String destinationName) {
        this.destinationCredentialsProvider = buildDestinationCredentialsProvider(destinationName, httpClient, destinationServiceBinding);

        DestinationContext destinationContext = destinationCredentialsProvider.getDestinationContext();

        this.serviceRegion = new ServiceRegion(CF, destinationContext.getServiceUri());
        this.isCertificateAuthentication = destinationContext.isCertificateAuthentication();

        if(isCertificateAuthentication){
            this.keyStoreDetails = destinationCredentialsProvider.getKeyStoreDetails();
        }

        return build();
    }

    public AlertNotificationClient buildFromDestinationBinding(DestinationServiceBinding destinationServiceBinding, String destinationName, Long invalidationTime) {
        this.invalidationTime = invalidationTime;

        assertValidInvalidationTime();
        return buildFromDestinationBinding(destinationServiceBinding, destinationName);
    }

    private DestinationCredentialsProvider buildDestinationCredentialsProvider(String destinationName, HttpClient httpClient, DestinationServiceBinding destinationServiceBinding) {
        return new DestinationCredentialsProvider( //
                destinationName, //
                httpClient, //
                new OAuthAuthorizationHeader( //
                        requireNonNull(destinationServiceBinding.getClientId()), //
                        requireNonNull(destinationServiceBinding.getClientSecret()), //
                        requireNonNull(destinationServiceBinding.getOauthUri()), //
                        httpClient), //
                destinationServiceBinding //
        );
    }

    public AlertNotificationClient build() {
        return new AlertNotificationClient(
                requireNonNull(httpClient),
                requireNonNull(retryPolicy),
                requireNonNull(serviceRegion),
                buildAuthorizationHeader(),
                invalidationTime,
                keyStoreDetails,
                destinationCredentialsProvider,
                new HttpClientFactory(),
                isCertificateAuthentication
        );
    }

    private IAuthorizationHeader buildAuthorizationHeader() {
        if(nonNull(destinationCredentialsProvider) && !isCertificateAuthentication) {
            return destinationCredentialsProvider.getAuthorizationHeader();
        }

        if (isNull(username) && isNull(password)) {
            return null; // Rely on HttpClient configuration only
        }

        return isNull(oAuthServiceUri) //
                ? new BasicAuthorizationHeader(username, password) //
                : new OAuthAuthorizationHeader(username, password, oAuthServiceUri, httpClient);
    }

    private void assertValidInvalidationTime() {
        if(nonNull(invalidationTime) && invalidationTime < 0) {
            throw new ClientRequestException("InvalidationTime cannot be a negative number");
        }
    }
}