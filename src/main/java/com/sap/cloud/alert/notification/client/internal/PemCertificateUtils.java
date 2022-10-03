package com.sap.cloud.alert.notification.client.internal;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.sap.cloud.alert.notification.client.internal.KeyStoreUtils.decodeCertificateContent;

public class PemCertificateUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String BEGIN_CERTIFICATE_DELIMITER = "-----BEGIN CERTIFICATE-----";
    private static final String PRIVATE_KEY = "PRIVATE KEY";
    private static final String EMPTY_STRING = "";

    public static KeyStore generateKeyStore(KeyStoreDetails keyStoreDetails) throws Exception {
        String[] pemContent = decodeCertificateContent(keyStoreDetails.getKeyStoreContent()).split(BEGIN_CERTIFICATE_DELIMITER);
        String keyStorePassword = keyStoreDetails.getKeyStorePassword();

        Certificate[] certificateChain = Arrays.stream(pemContent) //
                .filter(entry -> !entry.contains(PRIVATE_KEY)) //
                .map(entry -> BEGIN_CERTIFICATE_DELIMITER + entry) //
                .map(PemCertificateUtils::loadCertificate) //
                .collect(Collectors.toList()) //
                .toArray(new Certificate[pemContent.length - 1]);

        KeyStore keyStore = KeyStore.getInstance("JKS");

        keyStore.load(null, keyStorePassword.toCharArray());
        keyStore.setKeyEntry(extractCommonName((X509Certificate) certificateChain[0]), createPrivateKey(pemContent[0], keyStorePassword), keyStorePassword.toCharArray(), certificateChain);

        return keyStore;
    }

    public static KeyStore generateKeyStore(String certificate, String privateKey) throws Exception {
        String[] certificateContent = certificate.split(BEGIN_CERTIFICATE_DELIMITER);

        Certificate[] certificateChain = Arrays.stream(certificateContent)
                .skip(1) //
                .map(entry -> BEGIN_CERTIFICATE_DELIMITER + entry) //
                .map(PemCertificateUtils::loadCertificate) //
                .collect(Collectors.toList()) //
                .toArray(new Certificate[certificateContent.length - 1]);

        KeyStore keyStore = KeyStore.getInstance("JKS");

        keyStore.load(null, EMPTY_STRING.toCharArray());
        keyStore.setKeyEntry(extractCommonName((X509Certificate) certificateChain[0]), createPrivateKey(privateKey, EMPTY_STRING), EMPTY_STRING.toCharArray(), certificateChain);

        return keyStore;
    }

    private static Certificate loadCertificate(String certificatePem) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");

            final byte[] content = readCertificateBytes(certificatePem);

            return certificateFactory.generateCertificate(new ByteArrayInputStream(content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractCommonName(X509Certificate certificate) throws Exception {
        X500Name x500Name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN CN = x500Name.getRDNs(BCStyle.CN)[0];

        return IETFUtils.valueToString(CN.getFirst().getValue());
    }

    private static byte[] readCertificateBytes(String certificate) {
        try (PemReader pemReader = new PemReader(new StringReader(certificate))) {
            return pemReader.readPemObject().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PrivateKey createPrivateKey(String privateKey, String password) throws IOException, PKCSException {
        Object privateKeyObject = new PEMParser(new StringReader(privateKey)).readObject();

        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
        if (privateKeyObject instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) privateKeyObject;
            KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);
            return keyPair.getPrivate();
        } else if (privateKeyObject instanceof PrivateKeyInfo) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) privateKeyObject;
            return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
        } else if (privateKeyObject instanceof PKCS8EncryptedPrivateKeyInfo) {
            PKCS8EncryptedPrivateKeyInfo privateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) privateKeyObject;

            JcePKCSPBEInputDecryptorProviderBuilder builder = new JcePKCSPBEInputDecryptorProviderBuilder().setProvider("BC");
            InputDecryptorProvider decryptionProvider = builder.build(password.toCharArray());

            return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo.decryptPrivateKeyInfo(decryptionProvider));
        }else {
            throw new IllegalArgumentException("Unsupported private key format '" + privateKeyObject.getClass().getSimpleName() + '"');
        }
    }
}
