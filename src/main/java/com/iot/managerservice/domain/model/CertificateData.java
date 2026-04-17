package com.iot.managerservice.domain.model;

public record CertificateData(
        String id,             // UUID
        String displayName,
        DeviceType deviceType,
        String status,         // "VALID", "REVOKED", "EXPIRED"
        long emissionDate,
        long expirationDate,
        String privateKeyPem,  // Guardamos esto para poder re-descargar el ZIP
        String certificatePem
) {}