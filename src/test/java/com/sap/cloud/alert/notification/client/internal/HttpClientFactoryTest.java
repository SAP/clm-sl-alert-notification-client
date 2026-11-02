package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.builder.HttpClientConfig;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.sap.cloud.alert.notification.client.TestUtils.TEST_KEYSTORE_CONTENT_P12;
import static com.sap.cloud.alert.notification.client.TestUtils.TEST_KEYSTORE_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpClientFactoryTest {

    private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----\nMIIDCTCCAfGgAwIBAgIUKhNrKrOhYxE1hw0XmuM+R8YQW9swDQYJKoZIhvcNAQEL\nBQAwFDESMBAGA1UEAwwJbG9jYWxob3N0MB4XDTI2MTAyNzA5MjM0MVoXDTI3MTAy\nNzA5MjM0MVowFDESMBAGA1UEAwwJbG9jYWxob3N0MIIBIjANBgkqhkiG9w0BAQEF\nAAOCAQ8AMIIBCgKCAQEAqFrNETeaVhTkv7K+pv49dZ7RpqhZgWX5R1bVOFJvpZZs\nLPjNRVUN8DyXRk1eNr63Lkgi8Ue245Gz5LxxNm2gtS540MNddvQ09s+D0OUYQt75\n9M4KFBWYAh5EFX0M1SlCiOeNWPnalp+0enYiM+UZ42pE4/yuYi/knskAkuE+aN7o\nGyrXXxiMU6dGWzHi9vCgc6eVo2oAj9XQ1hpc2NnEIulKpZ+J9X/gAZHBdpXiZSmj\nDFpceNL9QQvOo5uu/HAcZ/LU5hIDdSUgVNAXkQ0QhYzzAIdJfJ3+d+HCLEhd8lSK\nkczLDsCVu8R0oor5V87MhiVz5LMyNKqC5/mVfYAcsQIDAQABo1MwUTAdBgNVHQ4E\nFgQUaCigr6xEsNFvUJ7TdyErjeemlokwHwYDVR0jBBgwFoAUaCigr6xEsNFvUJ7T\ndyErjeemlokwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAIBnE\n6aHXbDG/TwoBF4OpW5cXRS6r/QR6wVFldVqAejjtEKrJPC2eIXY6JJGwEJ0wRXZ6\nfem95cAbuMNr4o8E3qCGeX3U+hJsei3CJ1l58j38pjvgeuViP7SL1JzZSLRNbkO3\nWfy3amsc3G1LCR+T2FSykWzqWXrJXZ2Aurk50+h8p8TxE5ErvfRo1zsFKg5wYl5X\nu5ba+djTQUZvdJSiGVC+guiriv2zMFV8l9Asp/O1OgWxEdUl+RNxNW//s00qrpS6\nvReGafAQqTrmun/XbpD0ZLTK/mXHAn1uxIcUsdMsDZWvRte0F68qfaNMse5CCIWy\n+/fQryIQaucFmm0OCA==\n-----END CERTIFICATE-----";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCoWs0RN5pWFOS/\nsr6m/j11ntGmqFmBZflHVtU4Um+llmws+M1FVQ3wPJdGTV42vrcuSCLxR7bjkbPk\nvHE2baC1LnjQw1129DT2z4PQ5RhC3vn0zgoUFZgCHkQVfQzVKUKI541Y+dqWn7R6\ndiIz5RnjakTj/K5iL+SeyQCS4T5o3ugbKtdfGIxTp0ZbMeL28KBzp5WjagCP1dDW\nGlzY2cQi6Uqln4n1f+ABkcF2leJlKaMMWlx40v1BC86jm678cBxn8tTmEgN1JSBU\n0BeRDRCFjPMAh0l8nf534cIsSF3yVIqRzMsOwJW7xHSiivlXzsyGJXPkszI0qoLn\n+ZV9gByxAgMBAAECggEAAV8n0yHDOMh7DCcatIQQ1pQskSyhBqpAz5PEKtOWHz2x\nMP62dz/TPFK8D0zN7WK2RDlLfcohXsWNd0BdtXox1rDghF/t7PKrFBs6liSo6WuS\nBdktBEvTcen5Kn0n0hYZc0GpH8rwwyzDy08kDHNYiu2VdQ+WUQyJMsEaJ3g2s/AT\nzrmG+OVIQWMMG99jBDcFy03orz/lVe0L8KHi/6GVTRDQ19GGJUQu1P2I1ZQk4Q53\nr90x4eIZeJcUOiBCOMfdDmRvpqvHUHXnLFL5ve73aJG03v6S8BWNBy+5Pn9qt05M\nwRt06fK70w5dg7/mbr0YBmyiy19gDkN/+tgAaZ+PgQKBgQDm+XUcEyOKj8VqLBQ0\ngn0+D8v6ZbZmKPYD/C+4xvrU8ZzRk5oIMAfaFneWbaj4ikL34IZTdGPpJqwweKRz\nJd0nn+0Gfqt867cWrXP8KX8KuKzGY6ZRKWY7UxavEwqp5OXZs8r+8d5lebzDQfss\nuzhaC0XbmltRpOSEZJDXIhTl8QKBgQC6mHVVpeUKpjd2j43S4+XasDp9VToHStkz\nU3Ymc81AjRy328EusSO9xylan48AswbRLKDGTdqx7A+qcEwUqZNDF6Jy8EL4qzJn\nwGLlKlo2fhx6eH4xgR2zIxcrG4NVPFMJB52cmYLCrbzF5r7Ebk+IrjbgebmO34v2\njtQMJMDiwQKBgCZQJNvmVNPsK0wEScm4B3xe9G/xpULm8oAQuf3Fi9/8wA2HlVpc\nVUEYzvd18zxe+uLRLJb1TIllL9TOfkDXfArRnWihZGLRWsp5NS8T1w35OjeiWflG\ni721d6LM3sz1uibViwulJUXh3qzo6fguihdtnUXjIdi2KG3JnLkC30KRAoGBAJ0y\nES89We6VxwXXvCgdHCgW7L9njEJWeRK3TkLordKizr3JoT6ftreLUDhrUULKj4Sq\nrytArWvUrgHtJvsFPO15vL5N0G3Dc+xZ9DaRbnyX05ANcGpuFsIyXrNPM14cOuvR\ngaNK5BDz4qNqV+4N7Gz+L3b+tSc7BqOJAfTVBbZBAoGAMJi+IvzC+7eNukky3s4s\nZPxKITqszk0pgzk4cX9Kw5mQn2vNt0JntkPTq7naoqESlODNDOf8GigNht5wEYN0\nH7CbbqfgmtvkZ1Utv0W/P9WmWAexM7hIwyA+fQebWomzIahQJqG40P7H6dHuPcRK\nhfq3l05QTSjrV3DOEwBk57M=\n-----END PRIVATE KEY-----";

    private static final HttpClientConfig HTTP_CLIENT_CONFIG = HttpClientConfig.fromDefaultConfig() //
            .connectionObtainTimeout(Duration.ofSeconds(30)) //
            .connectionEstablishTimeout(Duration.ofSeconds(20)) //
            .socketTimeout(Duration.ofSeconds(10)) //
            .build();

    private static final HttpClientFactory CLASS_UNDER_TEST = new HttpClientFactory(HTTP_CLIENT_CONFIG);

    @Test
    void whenCreateHttpClient_thenCorrectHttpClientReturned() {
        HttpClient httpClient = CLASS_UNDER_TEST.createHttpClient();
        // Not much to verify here without abusing reflection
        assertNotNull(httpClient);
    }

    @Test
    void whenCreateHttpClientWithKeyStoreDetailsIsCalled_thenCorrectHttpClientReturned() {
        KeyStoreDetails keyStoreDetails = new KeyStoreDetails(KeyStoreType.PKCS12, TEST_KEYSTORE_PASSWORD, TEST_KEYSTORE_CONTENT_P12);

        HttpClient result = CLASS_UNDER_TEST.createHttpClient(keyStoreDetails);
        // Not much to verify here without abusing reflection
        assertNotNull(result);
    }

    @Test
    void whenCreateHttpClientWithCertificateAndPrivateKeyIsCalled_thenCorrectHttpClientReturned() {
        HttpClient result = CLASS_UNDER_TEST.createHttpClient(CERTIFICATE, PRIVATE_KEY);
        // Not much to verify here without abusing reflection
        assertNotNull(result);
    }
}
