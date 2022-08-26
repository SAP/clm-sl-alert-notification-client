package com.sap.cloud.alert.notification.client.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.*;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.APPLICATION_JSON;
import static com.sap.cloud.alert.notification.client.internal.AlertNotificationClientUtils.EMPTY;
import static com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter.PAGE;
import static com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter.PAGE_SIZE;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.*;
import static org.apache.http.util.TextUtils.isBlank;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlertNotificationConfigurationClientTest {

    private static final String TEST_PAGE_QUERY = "3";
    private static final String TEST_PAGE_SIZE_QUERY = "50";
    private static final String TEST_STATUS_LINE = "TEST_STATUS_LINE";
    private static final String TEST_AUTHORIZATION_HEADER = "Basic TEST_AUTHORIZATION_HEADER";
    private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);
    private static final KeyStoreDetails TEST_KEYSTORE_DETAILS_PEM = new KeyStoreDetails(KeyStoreType.PEM, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_PEM);
    private static final KeyStoreDetails TEST_KEYSTORE_DETAILS_P12 = new KeyStoreDetails(KeyStoreType.PKCS12, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_P12);
    private static final String TEST_CERTIFICATE = "-----BEGIN CERTIFICATE-----\nMIIFwjCCA6qgAwIBAgIRAJEJ6qwdQRbTeiKhUcDLNh4wDQYJKoZIhvcNAQELBQAw\ngYAxCzAJBgNVBAYTAkRFMRQwEgYDVQQHDAtFVTEwLUNhbmFyeTEPMA0GA1UECgwG\nU0FQIFNFMSMwIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czElMCMG\nA1UEAwwcU0FQIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTAeFw0yMjA5MTkwNzM0\nNDBaFw0yMzA5MTkwODM0NDBaMIGnMQswCQYDVQQGEwJERTEPMA0GA1UEChMGU0FQ\nIFNFMSMwIQYDVQQLExpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czEPMA0GA1UE\nCxMGQ2FuYXJ5MRAwDgYDVQQLEwdzYXAtdWFhMRAwDgYDVQQHEwdzYXAtdWFhMS0w\nKwYDVQQDEyQzZDc0NjFkYy1hOTkwLTQ0NWMtODA3OC0zMDlmOTM3NmY5MDgwggEi\nMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDLg1TJHDGYXGFj8fU4agx57EZ5\nSyH86PG2GtvBcpcBII1y727NqpWn9HXFO2GdQqvzyN3ApU2b/tqDqqfUxdjbfOn8\nPywlmQrPB35R53InN/6tiFP5YQ3XRdquNXWs29864/m3r5WwUcYarwEBqEEwkOga\nlwbCZwk9Dn4z9FsT+/RtWKEcSoFwye0blNK2Yg3rxvt8Y8js8vszbtalQr61Q/9V\nW5Cf+6zS7sHciSF1ccdl894DPEE5vz+yq5r3jfrC63E60egyTYi7W6+B5R9yE3Xd\nwJxN9j5WKZHIICzR6BW+UD2K2neWw/aX/3I315bkOloNT7tiyne7b2+Sgzq5AgMB\nAAGjggEMMIIBCDAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMwghFuHFYs3+X6Hsck+\nw2+VBG6RMB0GA1UdDgQWBBRajDFtHBL1GYr1szPHXzggOeTxozAOBgNVHQ8BAf8E\nBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUHAwIwgZUGA1UdHwSBjTCBijCBh6CBhKCB\ngYZ/aHR0cDovL3NhcC1jbG91ZC1wbGF0Zm9ybS1jbGllbnQtY2EtZXUxMC1jYW5h\ncnktY3Jscy5zMy5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbS9jcmwvZjhhYmU1\nNGUtMTk1MS00NzBlLWFlMmQtZGU0MGMxNjMzNDFjLmNybDANBgkqhkiG9w0BAQsF\nAAOCAgEAGP4FkEtboIKBsda4DM3caKALOzMJM3Sk7zSbl5ryWcSzBpoTWB1mSGuh\nZct0+wWT8fWzNhKuMNyVMc2ih3HNSgmmnn0RUT0PmgZcy90EX6E7ynnyiOqcTtwS\nECylUSXsER1pq9BoQNQ5OGWE1PHFOgxTIeke//yY7S19NpbwQpPm/y+07rOW6T3z\nfUl83NVpIWffpK8jx1i3itpN6yTzieR9RrPYI6FaLoHCNCgZE8BEHHhJlS/CRTyT\nB1wO7qaiDMMeAaragXEInFJldOkWqzaPhptcISMcglp7sGKbNHT88e5piumCe1M+\n8XmRYbOSTObnsIIcNAqlk3w33s2qE85jRlodApXh/Tw4urhN78449ou3pLmwobM0\ny0xE4mOPfSUJvl801JeylHjO82PjZvR/TKurPSpL/fgkfFnMPiikIF3fiby/cxwj\n9dl6E3P5KUbpd7m4Ovno1AN4jCcD4NNZ4PDHaKvB4rZOE4myEujteSXrfDmigpLC\nuBVvfOc7gNEKM2oOq04e/ocwrWTSKfgO3wQCd0PuyMRlf5Z5/oeXogL0Ors8rXnq\n23RdVUTQn+CMPvSfzoFiXbf+6qHFjZtmZlFvTYZ8QsnLbtP1E5OVAqwiNCvAZK9n\nPXEDh5hMmzfz9KYiDB/mZSWIWitB/6u5iUWpsIQ1bJ2Pq42rfB0=\n-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\nMIIGaDCCBFCgAwIBAgITcAAAAAWaX7qDX+136AAAAAAABTANBgkqhkiG9w0BAQsF\nADBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNB\nUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMjAwNjIzMDg0NzI4\nWhcNMzAwNjIzMDg1NzI4WjCBgDELMAkGA1UEBhMCREUxFDASBgNVBAcMC0VVMTAt\nQ2FuYXJ5MQ8wDQYDVQQKDAZTQVAgU0UxIzAhBgNVBAsMGlNBUCBDbG91ZCBQbGF0\nZm9ybSBDbGllbnRzMSUwIwYDVQQDDBxTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50\nIENBMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAuaFK8HPApmtIk85H\nnVAkA+OAYlhH/gJe02iZvP1bQ2K7Y45W7Si/QxxIpgLTvN63/e8+O+2uW/jBkoAr\nWUCgIYk7KonSuQtsPlFlhwRAnW3Qet687mNGA50eQtx243dySrsDUU2yUIDyous+\nIHaBAWw1qyYAO1X6PYUOywYiQ0adLRZ/BHMXiR2Z/TCtDe6A4H+2EV0kxavtzZf4\n5/SBXpFlAw7MVYPd+FT3mv3xHzPu/+PqY/lTIQgPokXVNV6kp0H29fu45N1s4WIo\nG9U6EBWdS2aA9W1BefRwkjB3t/OJ6lQkBrSzhkGcYL51UKKpHY1nXaLSH9lA2sjj\nsQxzdggarsk0wBP8fPnlFEHTSLb//b++dZVeQm5MBsITl2uGLvAjI2vtLsFQx+R2\nAdPyJasCuIAtVla/+A41Vt09h4jSv2d3KgrYG6KQt1FI+SFElu7swN0lF3rQku8v\nnmhB9s+J/3EgUlNmGnirfj4MflplaHJpkSenl/B9QHmIl58IKpvwtdEwZ7AQmb03\nKnyCtIHDQ+4Q5OHplGa0bQOGIz3il3eReheE+lXHS9Cyran/++/mRip7/VdWk9Sf\nVv5Vnd+LXm/E74jvgr0km0jDw3qkcsOwAn6lcvfJPXF3t2Fb7BxHCfDiU7keSoy7\nXQpkWezzzN08vGoG5NHfKVdibxsCAwEAAaOCAQswggEHMBIGA1UdEwEB/wQIMAYB\nAf8CAQAwHQYDVR0OBBYEFMwghFuHFYs3+X6Hsck+w2+VBG6RMB8GA1UdIwQYMBaA\nFBy8ZisOyo1Ln42TcakPymdGaRMiMEoGA1UdHwRDMEEwP6A9oDuGOWh0dHA6Ly9j\nZHAucGtpLmNvLnNhcC5jb20vY2RwL1NBUCUyMENsb3VkJTIwUm9vdCUyMENBLmNy\nbDBVBggrBgEFBQcBAQRJMEcwRQYIKwYBBQUHMAKGOWh0dHA6Ly9haWEucGtpLmNv\nLnNhcC5jb20vYWlhL1NBUCUyMENsb3VkJTIwUm9vdCUyMENBLmNydDAOBgNVHQ8B\nAf8EBAMCAQYwDQYJKoZIhvcNAQELBQADggIBADZygpVfAAC7dTaoKeEJ/8T8zeHX\nR93AEV2m52aX6yXCfzwkL92cW1zBsCuNi82K9PiNmzb/WVB5i7VdXUwAd7bI9ACb\n0O/WkNHU+XB9Ta3VPQE14XL7jMaNHVLeaXA3iYcWqeuKQkYPHdMluBqcGmaYXnS0\nXSLocl+zRx0KMbQjvxCpGlf9XP52qqKyb1Gay152Kg2b+RmiKGqCBEHEoo2dXo/A\nD3N/Ei1CWkh/4hAw+scyyVC3S7L8ZyiLvaDYg013nt09S9wIIaB6Tub1+y2lK3PW\nHRVK9FEWraabKKVSOOXtrt+eVOCVJJwC7XjwFBywu2EgYuomoPf6qgcqWIr4cmBD\nqsHiAE3OygknSn2k97ooFGHTsyVt0AInhgVIk38Wip6F275JwX2xYMyyu0YiQEPT\n5HdAoWcBIl4v6wZz1hWlF4FDD7zDns11ZCeLdCHss9NV8WJ6ClYNSQArtbIoYD1Y\n9RzJr9LIlRPK82fM9b6peKQ2XUrTkMLFkIiI1HpT+Nt3JgtY/uDkXIV9nlXckDj6\nu9msfW8J9HU+cBQKAjfl1BoyLijQaXGoSvirJQSwh1Q9zLuH25uCkxhejZ8cDJrq\np55i444meVi6Xf66WaHPWyJunQpN/zb14ZpMNB6PFp94gYSxPVyMhVWyCGK5C8mZ\n2JX4S0blcGoU+np5\n-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\nMIIFZjCCA06gAwIBAgIQGHcPvmUGa79M6pM42bGFYjANBgkqhkiG9w0BAQsFADBN\nMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNBUCBT\nRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMTkwMjEzMTExOTM2WhcN\nMzkwMjEzMTEyNjMyWjBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYx\nDzANBgNVBAoMBlNBUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwggIi\nMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQChbHLXJoe/zFag6fB3IcN3d3HT\nY14nSkEZIuUzYs7B96GFxQi0T/2s971JFiLfB4KaCG+UcG3dLXf1H/wewq8ahArh\nFTsu4UR71ePUQiYlk/G68EFSy2zWYAJliXJS5k0DFMIWHD1lbSjCF3gPVJSUKf+v\nHmWD5e9vcuiPBlSCaEnSeimYRhg0ITmi3RJ4Wu7H0Xp7tDd5z4HUKuyi9XRinfvG\nkPALiBaX01QRC51cixmo0rhVe7qsNh7WDnLNBZeA0kkxNhLKDl8J6fQHKDdDEzmZ\nKhK5KxL5p5YIZWZ8eEdNRoYRMXR0PxmHvRanzRvSVlXSbfqxaKlORfJJ1ah1bRNt\no0ngAQchTghsrRuf3Qh/2Kn29IuBy4bjKR9CdNLxGrClvX/q26rUUlz6A3lbXbwJ\nEHSRnendRfEiia+xfZD+NG2oZW0IdTXSqkCbnBnign+uxGH5ECjuLEtvtUx6i9Ae\nxAvK2FqIuud+AchqiZBKzmQAhUjKUoACzNP2Bx2zgJOeB0BqGvf6aldG0n2hYxJF\n8Xssc8TBlwvAqtiubP/UxJJPs+IHqU+zjm7KdP6dM2sbE+J9O3n8DzOP0SDyEmWU\nUCwnmoPOQlq1z6fH9ghcp9bDdbh6adXM8I+SUYUcfvupOzBU7rWHxDCXld/24tpI\nFA7FRzHwKXqMSjwtBQIDAQABo0IwQDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/\nBAUwAwEB/zAdBgNVHQ4EFgQUHLxmKw7KjUufjZNxqQ/KZ0ZpEyIwDQYJKoZIhvcN\nAQELBQADggIBABdSKQsh3EfVoqplSIx6X43y2Pp+kHZLtEsRWMzgO5LhYy2/Fvel\neRBw/XEiB5iKuEGhxHz/Gqe0gZixw3SsHB1Q464EbGT4tPQ2UiMhiiDho9hVe6tX\nqX1FhrhycAD1xHIxMxQP/buX9s9arFZauZrpw/Jj4tGp7aEj4hypWpO9tzjdBthy\n5vXSviU8L2HyiQpVND/Rp+dNJmVYTiFLuULRY28QbikgFO2xp9s4RNkDBnbDeTrT\nCKWcVsmlZLPJJQZm0n2p8CvoeAsKzIULT9YSbEEBwmeqRlmbUaoT/rUGoobSFcrP\njrBg66y5hA2w7S3tDH0GjMpRu16b2u0hYQocUDuMlyhrkhsO+Qtqkz1ubwHCJ8PA\nRJw6zYl9VeBtgI5F69AEJdkAgYfvPw5DJipgVuQDSv7ezi6ZcI75939ENGjSyLVy\n4SuP99G7DuItG008T8AYFUHAM2h/yskVyvoZ8+gZx54TC9aY9gPIKyX++4bHv5BC\nqbEdU46N05R+AIBW2KvWozQkjhSQCbzcp6DHXLoZINI6y0WOImzXrvLUSIm4CBaj\n6MTXInIkmitdURnmpxTxLva5Kbng/u20u5ylIQKqpcD8HWX97lLVbmbnPkbpKxo+\nLvHPhNDM3rMsLu06agF4JTbO8ANYtWQTx0PVrZKJu+8fcIaUp7MVBIVZ\n-----END CERTIFICATE-----\n";
    private static final String TEST_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAy4NUyRwxmFxhY/H1OGoMeexGeUsh/OjxthrbwXKXASCNcu9u\nzaqVp/R1xTthnUKr88jdwKVNm/7ag6qn1MXY23zp/D8sJZkKzwd+UedyJzf+rYhT\n+WEN10XarjV1rNvfOuP5t6+VsFHGGq8BAahBMJDoGpcGwmcJPQ5+M/RbE/v0bVih\nHEqBcMntG5TStmIN68b7fGPI7PL7M27WpUK+tUP/VVuQn/us0u7B3IkhdXHHZfPe\nAzxBOb8/squa9436wutxOtHoMk2Iu1uvgeUfchN13cCcTfY+VimRyCAs0egVvlA9\nitp3lsP2l/9yN9eW5DpaDU+7Ysp3u29vkoM6uQIDAQABAoIBAAQaJJZdLB6/FfN2\nyBOYB6JZgd4mBoxbgavDBNT0Y1jReCht7RTWMGgPRGRyWvhZhK/IR7f/XP4c9/iZ\ncwKV7lYWQX0lJNWpLQ+ZPhGWkbV6qnaC/fZT33yMWukSCMowMGK2f60VK/Y+37hf\n+Kw44P+CnDsU+jzm6MfDSAXyEffoHgLvCEUbh5oK7+4dry8cI+V2nosZeKn/WZ7t\nSChqRHtDnbYkdGhOH+Ac+rKxTZPSua0ylNEtQUVeRaM3xk29EIsWqj6GxNSD8oFL\n3vXgmq72xIC94dTSeCJNf17Jm7niDQXZn4IMNGjPXtyONJwARF0wPngGhwzgS6RT\nkdYhyMUCgYEA7MxMYqDoTWRm34QktyGjljCWZ+jLpv6SlX/9zZ+HnzxmphF9ZtQa\nE2ck4DED/XGnKHBr965T5iLdwGWXEYHeYHaZxPxMYlILsULmjdeoCDj2GvYAWEf7\nnNPxpWap/zrlJipBbj5GdYVME1L5ezzavpVKKDs4KBIlK+J203JqDJMCgYEA3AQR\nPkLVwpGcnWAWhHZzFMevs/6QntYmVV5irZxrTOkNOF3/Y5fXfLGVA0J7+ugFPkWE\naJtvJhAvydD5HHLmx2wPhmrlxM2yPMbe5HmuWccTxM+laUnxJMAcFKJi5KDwJ/2+\nUQKa5WJSjKiepDDizlYvPl1t/gQ5cnxMGleYtwMCgYB7HbVRSku9gUgjSjc0p96/\nxb9NgzHvP0jDReqVsC35UpQkH8/NWNW95NC9Z36llSPN2LWp7w9cBiC5WZhz18vg\n54kHbA3iyLmfjiME+G066TK7zc9cFwDxBxkKYBhexSZC85FVWjeT/pwRKADiXD92\n+3O0+yU1YEnHSVVylngg1QKBgH0hTxFC/8H2AMW8tXHG0DK8UyCiomvDze91i9fD\ng38teJhbVXm2DRddBCvjbxHHTdwZu3GnHTLft94nHNbiPoCi472GJIGmnz1Tucbl\nsZRb1dF0a1YTeLN3E0FlDauMIKoN9WSrf58AKYTYDcnCB+xkNeBZUMpMasPDD6FX\nuoIzAoGBAJj/fIUcL/g0J2bkRKr1wfOiYd8PC6QtZHlYXQQmXlKuelUXBh2ECIcg\nYB/llhtO3YfJxPIskbTfkOTs7fL8GbOq4wcYnW6uAl7m61YVrzNQY0F5D6PvSfd8\nlJJ5M7cgg93YtwKPkkC054F/DegN/aBzsLHZkE78nms4dPdeVYdh\n-----END RSA PRIVATE KEY-----\n";

    private static final Action TEST_ACTION = new Action(TEST_TYPE, TEST_NAME, TEST_STATE, TEST_DESCRIPTION, TEST_LABELS, TEST_DISCARD_AFTER, TEST_FALLBACK_TIME, TEST_FALLBACK_ACTION, TEST_PROPERTIES);
    private static final Subscription TEST_SUBSCRIPTION = new Subscription(TEST_NAME, TEST_STATE, TEST_TIMESTAMP, TEST_DESCRIPTION, TEST_LABELS, TEST_ACTIONS, TEST_CONDITIONS);
    private static final Configuration TEST_CONFIGURATION = new Configuration(singletonList(TEST_ACTION), singletonList(TEST_CONDITION), singletonList(TEST_SUBSCRIPTION));
    private static final AlertNotificationClientUtils.ErrorHttpResponse ERROR_HTTP_RESPONSE = new AlertNotificationClientUtils.ErrorHttpResponse(EMPTY);
    private static final ConfigurationResponse<Action> ACTION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(singletonList(TEST_ACTION), TEST_CONFIGURATION_PAGING_METADATA);
    private static final ConfigurationResponse<Condition> CONDITION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(singletonList(TEST_CONDITION), TEST_CONFIGURATION_PAGING_METADATA);
    private static final ConfigurationResponse<Subscription> SUBSCRIPTION_CONFIGURATION_RESPONSE = new ConfigurationResponse<>(singletonList(TEST_SUBSCRIPTION), TEST_CONFIGURATION_PAGING_METADATA);

    private static final String ACTION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/action/%s";
    private static final String ACTIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/action", TEST_SERVICE_REGION.getPlatform().getKey());
    private static final String CONDITION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/condition/%s";
    private static final String CONDITIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/condition", TEST_SERVICE_REGION.getPlatform().getKey());
    private static final String SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE = "/%s/configuration/v1/subscription/%s";
    private static final String SUBSCRIPTIONS_CONFIGURATION_PATH = format("/%s/configuration/v1/subscription", TEST_SERVICE_REGION.getPlatform().getKey());
    private static final String CONFIGURATION_MANAGEMENT_BASE_PATH = format("/%s/configuration/v1/configuration", TEST_SERVICE_REGION.getPlatform().getKey());

    private DestinationCredentialsProvider mockedDestinationCredentialsProvider;
    private HttpClientFactory mockedHttpClientFactory;
    private HttpClient mockedHttpClient;
    private AlertNotificationConfigurationClient classUnderTest;
    private ArgumentCaptor<HttpUriRequest> requestCaptor;
    private IAuthorizationHeader mockedAuthorizationHeader;
    private Map<ConfigurationQueryParameter, String> requestParameters;

    @BeforeEach
    public void setUp() {
        mockedHttpClient = mock(HttpClient.class);
        mockedHttpClientFactory = mock(HttpClientFactory.class);
        mockedDestinationCredentialsProvider = mock(DestinationCredentialsProvider.class);
        mockedAuthorizationHeader = mock(IAuthorizationHeader.class);
        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                mockedAuthorizationHeader //
        );

        requestParameters = new HashMap<>();
        requestParameters.put(PAGE_SIZE, TEST_PAGE_SIZE_QUERY);
        requestParameters.put(PAGE, TEST_PAGE_QUERY);

        doReturn(TEST_AUTHORIZATION_HEADER).when(mockedAuthorizationHeader).getValue();
    }

    @Test
    public void whenGetConditionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, CONDITION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Condition> conditions = classUnderTest.getConditions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(CONDITION_CONFIGURATION_RESPONSE, conditions);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenGetConditionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, CONDITION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        ConfigurationResponse<Condition> conditions = classUnderTest.getConditions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(CONDITION_CONFIGURATION_RESPONSE, conditions);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication1_whenGetConditionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, CONDITION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        ConfigurationResponse<Condition> conditions = classUnderTest.getConditions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(CONDITION_CONFIGURATION_RESPONSE, conditions);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication2_whenGetConditionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, CONDITION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        ConfigurationResponse<Condition> conditions = classUnderTest.getConditions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(CONDITION_CONFIGURATION_RESPONSE, conditions);
    }

    @Test
    public void givenThatRequestFails_whenGetConditionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getConditions(requestParameters));
    }

    @Test
    public void whenCreatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Condition condition = classUnderTest.createCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }


    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenCreatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Condition condition = classUnderTest.createCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }
    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication1_whenCreatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.createCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication2_whenCreatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.createCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONDITIONS_CONFIGURATION_PATH), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenCreatingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createCondition(TEST_CONDITION));
    }

    @Test
    public void whenGetConditionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Condition condition = classUnderTest.getCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromCertificateServiceBinding_andCertificateAuthentication_whenGetConditionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Condition condition = classUnderTest.getCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication1_whenGetConditionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.getCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication2_whenGetConditionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.getCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenGetConditionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getCondition(TEST_CONDITION.getName()));
    }

    @Test
    public void whenUpdatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Condition condition = classUnderTest.updateCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName()), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromCertificateServiceBinding_andCertificateAuthentication_whenUpdatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Condition condition = classUnderTest.updateCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName()), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication1_whenUpdatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(TEST_KEYSTORE_DETAILS_PEM).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_PEM);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_PEM,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.updateCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName()), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenFromDestinationBinding_andCertificateAuthentication2_whenUpdatingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_CONDITION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Condition condition = classUnderTest.updateCondition(TEST_CONDITION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName()), TEST_CONDITION) //
        );
        assertEquals(TEST_CONDITION, condition);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateCondition(TEST_CONDITION));
    }

    @Test
    public void whenDeletingCondition_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteCondition(TEST_CONDITION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, CONDITION_CONFIGURATION_PATH_TEMPLATE, TEST_CONDITION.getName())) //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingCondition_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteCondition(TEST_CONDITION.getName()));
    }

    @Test
    public void whenGetActionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, ACTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Action> actions = classUnderTest.getActions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(ACTION_CONFIGURATION_RESPONSE, actions);

    }

    @Test
    public void givenThatRequestFails_whenGetActionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getActions(requestParameters));
    }

    @Test
    public void whenCreatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Action action = classUnderTest.createAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH), TEST_ACTION) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenCreatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Action action = classUnderTest.createAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH), TEST_ACTION) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenCreatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Action action = classUnderTest.createAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, ACTIONS_CONFIGURATION_PATH), TEST_ACTION) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenCreatingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createAction(TEST_ACTION));
    }

    @Test
    public void whenGetActionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Action action = classUnderTest.getAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenGetActionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Action action = classUnderTest.getAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenGetActionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Action action = classUnderTest.getAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenGetActionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getAction(TEST_ACTION.getName()));
    }

    @Test
    public void whenUpdatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Action action = classUnderTest.updateAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName()), TEST_ACTION)
        //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenUpdatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Action action = classUnderTest.updateAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName()), TEST_ACTION)
                //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenUpdatingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_ACTION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Action action = classUnderTest.updateAction(TEST_ACTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName()), TEST_ACTION)
                //
        );
        assertEquals(TEST_ACTION, action);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateAction(TEST_ACTION));
    }

    @Test
    public void whenDeletingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenDeletingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        classUnderTest.deleteAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenDeletingAction_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.deleteAction(TEST_ACTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, ACTION_CONFIGURATION_PATH_TEMPLATE, TEST_ACTION.getName())) //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingAction_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteAction(TEST_ACTION.getName()));
    }

    @Test
    public void whenGetSubscriptionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, SUBSCRIPTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        ConfigurationResponse<Subscription> subscriptions = classUnderTest.getSubscriptions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(SUBSCRIPTION_CONFIGURATION_RESPONSE, subscriptions);

    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenGetSubscriptionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, SUBSCRIPTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        ConfigurationResponse<Subscription> subscriptions = classUnderTest.getSubscriptions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(SUBSCRIPTION_CONFIGURATION_RESPONSE, subscriptions);

    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenGetSubscriptionsIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, SUBSCRIPTION_CONFIGURATION_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        ConfigurationResponse<Subscription> subscriptions = classUnderTest.getSubscriptions(requestParameters);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH, requestParameters)) //
        );
        assertEquals(SUBSCRIPTION_CONFIGURATION_RESPONSE, subscriptions);

    }

    @Test
    public void givenThatRequestFails_whenGetSubscriptionsIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getSubscriptions(requestParameters));
    }

    @Test
    public void whenCreatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Subscription subscription = classUnderTest.createSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenCreatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Subscription subscription = classUnderTest.createSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenCreatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Subscription subscription = classUnderTest.createSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTIONS_CONFIGURATION_PATH), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenCreatingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.createSubscription(TEST_SUBSCRIPTION));
    }

    @Test
    public void whenGetSubscriptionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Subscription subscription = classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName())) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenGetSubscriptionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Subscription subscription = classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName())) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenGetSubscriptionIsCalled_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Subscription subscription = classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName())) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenGetSubscriptionIsCalled_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.getSubscription(TEST_SUBSCRIPTION.getName()));
    }

    @Test
    public void whenUpdatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPut.class));

        Subscription subscription = classUnderTest.updateSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenUpdatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Subscription subscription = classUnderTest.updateSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenUpdatingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPut.class);
        doReturn(buildResponse(SC_OK, TEST_SUBSCRIPTION)).when(mockedHttpClient).execute(any(HttpPut.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Subscription subscription = classUnderTest.updateSubscription(TEST_SUBSCRIPTION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPut) requestCaptor.getValue(), //
                buildExpectedPutRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()), TEST_SUBSCRIPTION) //
        );
        assertEquals(TEST_SUBSCRIPTION, subscription);
    }

    @Test
    public void givenThatRequestFails_whenUpdatingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPut.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.updateSubscription(TEST_SUBSCRIPTION));
    }

    @Test
    public void whenDeletingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));

        classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()))
        //
        );
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenDeletingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()))
                //
        );
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenDeletingSubscription_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpDelete.class);
        doReturn(buildResponse(SC_NO_CONTENT, EMPTY)).when(mockedHttpClient).execute(any(HttpDelete.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName());

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpDelete) requestCaptor.getValue(), //
                buildExpectedDeleteRequest(toRequestURI(TEST_SERVICE_URI, SUBSCRIPTION_CONFIGURATION_PATH_TEMPLATE, TEST_SUBSCRIPTION.getName()))
                //
        );
    }

    @Test
    public void givenThatRequestFails_whenDeletingSubscription_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpDelete.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.deleteSubscription(TEST_SUBSCRIPTION.getName()));
    }

    @Test
    public void whenImportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpPost.class));

        Configuration result = classUnderTest.importConfiguration(TEST_CONFIGURATION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH), TEST_CONFIGURATION) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenImportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Configuration result = classUnderTest.importConfiguration(TEST_CONFIGURATION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH), TEST_CONFIGURATION) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenImportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpPost.class);
        doReturn(buildResponse(SC_CREATED, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpPost.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Configuration result = classUnderTest.importConfiguration(TEST_CONFIGURATION);

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpPost) requestCaptor.getValue(), //
                buildExpectedPostRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH), TEST_CONFIGURATION) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenThatRequestFails_whenImportingAll_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpPost.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.importConfiguration(TEST_CONFIGURATION));
    }

    @Test
    public void whenExportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpGet.class));

        Configuration result = classUnderTest.exportConfiguration();

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH)) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenFromCertificateServiceBinding_withCertificateAuthentication_whenExportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_CERTIFICATE, TEST_PRIVATE_KEY);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                TEST_CERTIFICATE, //
                TEST_PRIVATE_KEY, //
                mockedHttpClientFactory, //
                true //
        );

        Configuration result = classUnderTest.exportConfiguration();

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH)) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenFromDestinationBinding_withCertificateAuthentication_whenExportingAll_thenCorrectRequestIsSent() throws Exception {
        requestCaptor = forClass(HttpGet.class);
        doReturn(buildResponse(SC_OK, TEST_CONFIGURATION)).when(mockedHttpClient).execute(any(HttpGet.class));
        doReturn(TEST_KEYSTORE_DETAILS_P12).when(mockedDestinationCredentialsProvider).getKeyStoreDetails();
        doReturn(mockedHttpClient).when(mockedHttpClientFactory).createHttpClient(TEST_KEYSTORE_DETAILS_P12);

        classUnderTest = new AlertNotificationConfigurationClient( //
                mockedHttpClient, //
                TEST_RETRY_POLICY, //
                TEST_SERVICE_REGION, //
                null, //
                null,
                TEST_KEYSTORE_DETAILS_P12,
                mockedDestinationCredentialsProvider,
                mockedHttpClientFactory,
                true
        );

        classUnderTest.certificateExpirationTime = Long.MAX_VALUE;

        Configuration result = classUnderTest.exportConfiguration();

        verify(mockedHttpClient).execute(requestCaptor.capture());
        assertCorrectSSLRequest( //
                (HttpGet) requestCaptor.getValue(), //
                buildExpectedGetRequest(toRequestURI(TEST_SERVICE_URI, CONFIGURATION_MANAGEMENT_BASE_PATH)) //
        );
        assertEquals(TEST_CONFIGURATION, result);
    }

    @Test
    public void givenThatRequestFails_whenExportingAll_thenExceptionIsThrown() throws Exception {
        doReturn(buildResponse(SC_INTERNAL_SERVER_ERROR, ERROR_HTTP_RESPONSE)).when(mockedHttpClient).execute(any(HttpGet.class));

        assertThrows(ServerResponseException.class, () -> classUnderTest.exportConfiguration());
    }

    private static void assertCorrectRequest(HttpGet found, HttpGet expected) {
        assertCommonHttpRequestProperties(found, expected);
    }

    private static void assertCorrectSSLRequest(HttpGet found, HttpGet expected) {
        assertCommonSSLHttpRequestProperties(found, expected);
    }

    private static void assertCorrectRequest(HttpPost found, HttpPost expected) throws IOException {
        assertCommonHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectSSLRequest(HttpPost found, HttpPost expected) throws IOException {
        assertCommonSSLHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectRequest(HttpPut found, HttpPut expected) throws IOException {
        assertCommonHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectSSLRequest(HttpPut found, HttpPut expected) throws IOException {
        assertCommonSSLHttpRequestProperties(found, expected);
        assertEquals(APPLICATION_JSON, found.getFirstHeader(CONTENT_TYPE).getValue());
        assertEquals(EntityUtils.toString(expected.getEntity(), UTF_8.name()), EntityUtils.toString(found.getEntity(), UTF_8.name()));
    }

    private static void assertCorrectRequest(HttpDelete found, HttpDelete expected) {
        assertCommonHttpRequestProperties(found, expected);
    }

    private static void assertCorrectSSLRequest(HttpDelete found, HttpDelete expected) {
        assertCommonSSLHttpRequestProperties(found, expected);
    }

    private static void assertCommonHttpRequestProperties(HttpUriRequest found, HttpUriRequest expected) {
        assertURI(expected.getURI(), found.getURI());
        assertEquals(expected.getMethod(), found.getMethod());
        assertEquals(TEST_AUTHORIZATION_HEADER, found.getFirstHeader(AUTHORIZATION).getValue());
    }

    private static void assertCommonSSLHttpRequestProperties(HttpUriRequest found, HttpUriRequest expected) {
        assertURI(expected.getURI(), found.getURI());
        assertEquals(expected.getMethod(), found.getMethod());
        assertNull(found.getFirstHeader(AUTHORIZATION));
    }

    private static void assertURI(URI found, URI expected) {
        assertEquals(expected.getScheme().toLowerCase(), found.getScheme().toLowerCase());
        assertEquals(expected.getHost(), found.getHost());
        assertEquals(expected.getPort(), found.getPort());
        assertEquals(expected.getPath(), found.getPath());
        assertEquals(isBlank(expected.getQuery()), isBlank(found.getQuery()));
        if (isNotBlank(found.getQuery())) {
            assertEquals(new HashSet<>(asList(expected.getQuery().split("&"))), new HashSet<>(asList(found.getQuery().split("&"))));
        }
    }

    private static URI toRequestURI(URI uri, String path) throws URISyntaxException {
        return buildUri(uri, path, null);
    }

    private static URI toRequestURI(URI uri, String path, Map<ConfigurationQueryParameter, String> parameters) throws URISyntaxException {
        return buildUri(uri, path, toConfigurationQueryParameterPairs(parameters));
    }

    private static URI toRequestURI(URI uri, String path, String entityName) throws URISyntaxException {
        return buildUri(uri, format(path, TEST_SERVICE_REGION.getPlatform().getKey(), entityName), null);
    }

    private static HttpResponse buildResponse(int statusCode, Object responseEntity) {
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(PROTOCOL_VERSION, statusCode, TEST_STATUS_LINE));

        response.setEntity(new StringEntity(toJsonString(responseEntity), UTF_8.name()));

        return response;
    }

    private static HttpGet buildExpectedGetRequest(URI uri) {
        HttpGet request = new HttpGet(uri);

        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);

        return request;
    }

    private static HttpPost buildExpectedPostRequest(URI uri, Object payload) {
        HttpPost request = new HttpPost(uri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);
        request.setEntity(new StringEntity(toJsonString(payload), UTF_8.name()));

        return request;
    }

    private static HttpPut buildExpectedPutRequest(URI uri, Object payload) {
        HttpPut request = new HttpPut(uri);

        request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);
        request.setEntity(new StringEntity(toJsonString(payload), UTF_8.name()));

        return request;
    }

    private static HttpDelete buildExpectedDeleteRequest(URI uri) {
        HttpDelete request = new HttpDelete(uri);

        request.setHeader(AUTHORIZATION, TEST_AUTHORIZATION_HEADER);

        return request;
    }

    private static List<NameValuePair> toConfigurationQueryParameterPairs(Map<ConfigurationQueryParameter, String> queryFilters) {
        return queryFilters.entrySet().stream().map(queryFilter -> new BasicNameValuePair(queryFilter.getKey().getKey(), queryFilter.getValue())).collect(toList());
    }

    private static URI buildUri(URI uri, String path, List<NameValuePair> parameters) throws URISyntaxException {
        URIBuilder builder = new URIBuilder().setScheme(uri.getScheme()).setHost(uri.getHost()).setPort(uri.getPort()).setPath(path);

        return isNull(parameters) ? builder.build() : builder.setParameters(parameters).build();
    }
}
