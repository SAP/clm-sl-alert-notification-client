package com.sap.cloud.alert.notification.client.internal;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import static com.sap.cloud.alert.notification.client.internal.KeyStoreUtils.decodeCertificateContent;

class PemCertificateUtils {

    static {
        Security.addProvider(new BouncyCastleFipsProvider());
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

        Certificate[] certificateChain = Arrays.stream(certificateContent).skip(1) //
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
        String subjectDN = certificate.getSubjectX500Principal().getName();
        LdapName ldapName = new LdapName(subjectDN);

        for (Rdn rdn : ldapName.getRdns()) {
            if ("CN".equalsIgnoreCase(rdn.getType())) {
                return rdn.getValue().toString();
            }
        }
        return null;
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

            JcePKCSPBEInputDecryptorProviderBuilder builder = new JcePKCSPBEInputDecryptorProviderBuilder().setProvider("BCFIPS");
            InputDecryptorProvider decryptionProvider = builder.build(password.toCharArray());

            return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo.decryptPrivateKeyInfo(decryptionProvider));
        } else {
            throw new IllegalArgumentException("Unsupported private key format '" + privateKeyObject.getClass().getSimpleName() + '"');
        }
    }

    private static byte[] readCertificateBytes(String certificate) {
        String certificateContent = certificate //
                .replaceAll("-----BEGIN CERTIFICATE-----", "") //
                .replaceAll("-----END CERTIFICATE-----", "") //
                .replaceAll(System.lineSeparator(), ""); //

        return Base64.getDecoder().decode(certificateContent);
    }
//
//    private static PrivateKey createPrivateKey(String privateKey, String password) throws Exception {
//        String privateKeyPEM = privateKey //
//                .replace("-----BEGIN RSA PRIVATE KEY-----", "") //
//                .replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "") //
//                .replace("-----END RSA PRIVATE KEY-----", "") //
//                .replace("-----BEGIN PRIVATE KEY-----", "") //
//                .replace("-----END PRIVATE KEY-----", "") //
//                .replace("-----END ENCRYPTED PRIVATE KEY-----", "") //
//                .replaceAll("\\s+", "")
//                .replaceAll(System.lineSeparator(), "");
//
//        byte[] keyBytes = Base64.getMimeDecoder().decode(privateKeyPEM);
//
//        if (privateKey.contains("-----BEGIN ENCRYPTED PRIVATE KEY-----")) {
//            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(keyBytes);
//
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
//            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
//            SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
//
//            Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
//            cipher.init(Cipher.DECRYPT_MODE, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
//
//            // Decrypt the private key bytes
//            byte[] decryptedKeyBytes = cipher.doFinal(encryptedPrivateKeyInfo.getEncryptedData());
//
//            // Parse decrypted key as PKCS#8
//            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedKeyBytes);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            return kf.generatePrivate(keySpec);
//        }
//
//        if (privateKey.contains("BEGIN RSA PRIVATE KEY")) {
//            // PKCS#1 format, convert to PKCS#8
//            keyBytes = convertPKCS1toPKCS8(keyBytes);
//        }
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePrivate(keySpec);
//    }
//
//    private static byte[] convertPKCS1toPKCS8(byte[] pkcs1Bytes) throws IOException {
//        final byte[] rsaOID = new byte[] {
//                0x30, 0x0d, 0x06, 0x09, 0x2a, (byte)0x86, 0x48, (byte)0x86,
//                (byte)0xf7, 0x0d, 0x01, 0x01, 0x01, 0x05, 0x00
//        };
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        baos.write(0x02);
//        baos.write(0x01);
//        baos.write(0x00);
//
//        baos.write(rsaOID);
//
//        baos.write(0x04);
//        writeLength(baos, pkcs1Bytes.length);
//        baos.write(pkcs1Bytes);
//
//        byte[] inner = baos.toByteArray();
//        baos.reset();
//        baos.write(0x30);
//        writeLength(baos, inner.length);
//        baos.write(inner);
//
//        return baos.toByteArray();
//    }
//
//    private static void writeLength(ByteArrayOutputStream out, int length) {
//        if (length <= 127) {
//            out.write(length);
//        } else {
//            int numBytes = (Integer.SIZE - Integer.numberOfLeadingZeros(length) + 7) / 8;
//            out.write(0x80 | numBytes);
//            for (int i = numBytes - 1; i >= 0; i--) {
//                out.write((length >> (8 * i)) & 0xff);
//            }
//        }
//    }

}
