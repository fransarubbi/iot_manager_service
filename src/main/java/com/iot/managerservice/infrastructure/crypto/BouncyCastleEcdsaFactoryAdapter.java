package com.iot.managerservice.infrastructure.crypto;

import com.iot.managerservice.domain.model.CertificateRequest;
import com.iot.managerservice.domain.model.DeviceType;
import com.iot.managerservice.domain.model.GeneratedCertificate;
import com.iot.managerservice.domain.port.CertificateFactory;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.FileReader;
import java.io.File;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;


@Slf4j
@Component
public class BouncyCastleEcdsaFactoryAdapter implements CertificateFactory {

    private final PrivateKey caPrivateKey;
    private final X509Certificate caCertificate;

    public BouncyCastleEcdsaFactoryAdapter(
            @Value("${ca.cert.path:ca_keys/manager_ca.crt}") String certPath,
            @Value("${ca.key.path:ca_keys/manager_ca.key}") String keyPath) {

        Security.addProvider(new BouncyCastleProvider());

        try {
            if (!new File(certPath).exists() || !new File(keyPath).exists()) {
                log.warn("Archivos de la CA no encontrados en '{}' o '{}'. ¡La emisión fallará!", certPath, keyPath);
                this.caCertificate = null;
                this.caPrivateKey = null;
                return;
            }
            this.caCertificate = loadCertificate(certPath);
            this.caPrivateKey = loadPrivateKey(keyPath);
            log.info("Llaves de la Issuing CA cargadas exitosamente en memoria.");

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar las credenciales de la CA", e);
        }
    }

    private X509Certificate loadCertificate(String path) throws Exception {
        try (PEMParser pemParser = new PEMParser(new FileReader(path))) {
            Object parsedObj = pemParser.readObject();
            if (parsedObj instanceof X509CertificateHolder) {
                return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parsedObj);
            }
            throw new IllegalArgumentException("El archivo no contiene un certificado válido.");
        }
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        try (PEMParser pemParser = new PEMParser(new FileReader(path))) {
            Object parsedObj = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (parsedObj instanceof PEMKeyPair) {
                return converter.getPrivateKey(((PEMKeyPair) parsedObj).getPrivateKeyInfo());
            } else if (parsedObj instanceof org.bouncycastle.asn1.pkcs.PrivateKeyInfo) {
                return converter.getPrivateKey((org.bouncycastle.asn1.pkcs.PrivateKeyInfo) parsedObj);
            }
            throw new IllegalArgumentException("El archivo no contiene una llave privada válida.");
        }
    }

    @Override
    public GeneratedCertificate createCertificate(CertificateRequest request) {
        if (caPrivateKey == null || caCertificate == null) {
            throw new IllegalStateException("El Manager no tiene cargadas las llaves de la CA.");
        }

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            KeyPair deviceKeyPair = keyGen.generateKeyPair();

            X500Name subject = buildSubjectName(request);
            long now = System.currentTimeMillis();
            Date startDate = new Date(now);
            Date endDate = new Date(now + request.validityDays() * 86400000L);
            BigInteger serialNumber = new BigInteger(160, new SecureRandom());

            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    new X500Name(caCertificate.getSubjectX500Principal().getName()),
                    serialNumber, startDate, endDate, subject, deviceKeyPair.getPublic()
            );

            applyExtensions(certBuilder, request.deviceType(), request.sanDomain());

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA").build(caPrivateKey);
            X509CertificateHolder certHolder = certBuilder.build(signer);
            X509Certificate signedCert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

            return new GeneratedCertificate(
                    toPem("EC PRIVATE KEY", deviceKeyPair.getPrivate().getEncoded()),
                    toPem("CERTIFICATE", signedCert.getEncoded())
            );

        } catch (Exception e) {
            throw new RuntimeException("Error generando certificado mTLS ECDSA", e);
        }
    }

    private X500Name buildSubjectName(CertificateRequest req) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, req.commonName());
        if (req.organization() != null && !req.organization().isEmpty()) builder.addRDN(BCStyle.O, req.organization());
        if (req.country() != null && !req.country().isEmpty()) builder.addRDN(BCStyle.C, req.country());
        return builder.build();
    }

    private void applyExtensions(X509v3CertificateBuilder builder, DeviceType type, String domainOrSan) throws Exception {
        int ecdsaKeyUsage = KeyUsage.digitalSignature | KeyUsage.keyAgreement;
        builder.addExtension(Extension.keyUsage, true, new KeyUsage(ecdsaKeyUsage));
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        switch (type) {
            case EDGE:
                KeyPurposeId[] edgeUsages = { KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth };
                builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(edgeUsages));
                if (domainOrSan != null && !domainOrSan.isEmpty()) {
                    GeneralNames san = new GeneralNames(new GeneralName(GeneralName.dNSName, domainOrSan));
                    builder.addExtension(Extension.subjectAlternativeName, false, san);
                }
                break;
            case HUB:
                KeyPurposeId[] hubUsages = { KeyPurposeId.id_kp_clientAuth };
                builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(hubUsages));
                break;
            case ROUTER:
                KeyPurposeId[] routerUsages = { KeyPurposeId.id_kp_serverAuth };
                builder.addExtension(Extension.extendedKeyUsage, true, new ExtendedKeyUsage(routerUsages));
                if (domainOrSan != null && !domainOrSan.isEmpty()) {
                    GeneralNames san = new GeneralNames(new GeneralName(GeneralName.dNSName, domainOrSan));
                    builder.addExtension(Extension.subjectAlternativeName, false, san);
                }
                break;
        }
    }

    private String toPem(String type, byte[] data) throws Exception {
        StringWriter sw = new StringWriter();
        try (PemWriter pw = new PemWriter(sw)) {
            pw.writeObject(new PemObject(type, data));
        }
        return sw.toString();
    }
}