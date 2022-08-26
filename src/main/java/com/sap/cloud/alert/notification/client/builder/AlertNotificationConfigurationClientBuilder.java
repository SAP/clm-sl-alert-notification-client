package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationConfigurationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.internal.*;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.client.HttpClient;

import java.net.URI;

import static com.sap.cloud.alert.notification.client.Platform.CF;
import static java.util.Objects.*;

public final class AlertNotificationConfigurationClientBuilder {

    public static final IRetryPolicy DEFAULT_RETRY_POLICY = new SimpleRetryPolicy();

    private String username;
    private String password;
    private URI oAuthServiceUri;
    private HttpClient httpClient;
    private ServiceRegion serviceRegion;
    private IRetryPolicy retryPolicy = DEFAULT_RETRY_POLICY;
    private DestinationCredentialsProvider destinationCredentialsProvider;
    private boolean isCertificateAuthentication = false;
    private KeyStoreDetails keyStoreDetails;
    private Long invalidationTime;

    public AlertNotificationConfigurationClientBuilder withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withRetryPolicy(IRetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withServiceRegion(ServiceRegion serviceRegion) {
        this.serviceRegion = serviceRegion;

        return this;
    }

    public AlertNotificationConfigurationClientBuilder withAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public AlertNotificationConfigurationClientBuilder withAuthentication(String username, String password, URI oAuthServiceUri) {
        this.username = username;
        this.password = password;
        this.oAuthServiceUri = oAuthServiceUri;
        return this;
    }

    public IAlertNotificationConfigurationClient buildFromServiceBinding() {
        return buildFromServiceBinding(new AlertNotificationServiceBinding());
    }

    public IAlertNotificationConfigurationClient buildFromServiceBinding(AlertNotificationServiceBinding serviceBinding) {
        AlertNotificationConfigurationClientBuilder alertNotificationClientBuilder = withServiceRegion(new ServiceRegion(CF, serviceBinding.getServiceUri().toString()));

        return isNull(serviceBinding.getOauthUri()) //
                ? alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret()).build() //
                : alertNotificationClientBuilder.withAuthentication(serviceBinding.getClientId(), serviceBinding.getClientSecret(), serviceBinding.getOauthUri()).build();

    }

    public IAlertNotificationConfigurationClient buildFromDestinationBinding(DestinationServiceBinding destinationServiceBinding, String destinationName) {
        this.destinationCredentialsProvider = buildDestinationCredentialsProvider(destinationName, httpClient, destinationServiceBinding);

        DestinationContext destinationContext = destinationCredentialsProvider.getDestinationContext();

        this.serviceRegion = new ServiceRegion(CF, destinationContext.getServiceUri());
        this.isCertificateAuthentication = destinationContext.isCertificateAuthentication();

        if(isCertificateAuthentication){
            this.keyStoreDetails = destinationCredentialsProvider.getKeyStoreDetails();
        }

        return build();
    }

    public IAlertNotificationConfigurationClient buildFromDestinationBinding(DestinationServiceBinding destinationServiceBinding, String destinationName, Long invalidationTime) {
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

    public IAlertNotificationConfigurationClient build() {
        return new AlertNotificationConfigurationClient(
                requireNonNull(httpClient),
                requireNonNull(retryPolicy),
                requireNonNull(serviceRegion),
                buildAuthorizationHeader(),
                invalidationTime,
                keyStoreDetails,
                destinationCredentialsProvider,
                new HttpClientFactory(),
                false
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
