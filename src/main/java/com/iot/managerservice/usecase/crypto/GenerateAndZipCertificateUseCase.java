package com.iot.managerservice.usecase.crypto;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.model.CertificateRequest;
import com.iot.managerservice.domain.model.GeneratedCertificate;
import com.iot.managerservice.domain.port.CertificateFactory;
import com.iot.managerservice.domain.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class GenerateAndZipCertificateUseCase {

    private final CertificateFactory certificateFactory;
    private final CertificateRepository repository; // La variable final
    private final String caCertPath;

    public GenerateAndZipCertificateUseCase(
            CertificateFactory certificateFactory,
            CertificateRepository repository, // <-- Añadido aquí
            @Value("${ca.cert.path:ca_keys/manager_ca.crt}") String caCertPath) {

        this.certificateFactory = certificateFactory;
        this.repository = repository; // <-- Inicializado aquí
        this.caCertPath = caCertPath;
    }

    public byte[] execute(CertificateRequest request) {
        // Generamos los certificados con ECDSA
        GeneratedCertificate certs = certificateFactory.createCertificate(request);

        // Persistir para auditoría y futuras descargas
        long now = System.currentTimeMillis();
        long expiry = now + (request.validityDays() * 86400000L);
        String newId = UUID.randomUUID().toString();

        CertificateData dbData = new CertificateData(
                newId, request.displayName(), request.deviceType(), "VALID",
                now, expiry, certs.privateKeyPem(), certs.certificatePem()
        );
        repository.save(dbData);

        // Comprimimos al ZIP
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Archivo .key del dispositivo
            zos.putNextEntry(new ZipEntry(request.commonName() + ".key"));
            zos.write(certs.privateKeyPem().getBytes());
            zos.closeEntry();

            // Archivo .crt del dispositivo
            zos.putNextEntry(new ZipEntry(request.commonName() + ".crt"));
            zos.write(certs.certificatePem().getBytes());
            zos.closeEntry();

            // Incluir el certificado raíz (CA) en el ZIP
            Path caPath = Paths.get(caCertPath);
            if (Files.exists(caPath)) {
                zos.putNextEntry(new ZipEntry("manager_ca.crt"));
                zos.write(Files.readAllBytes(caPath));
                zos.closeEntry();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al comprimir los certificados en ZIP", e);
        }

        return baos.toByteArray();
    }
}