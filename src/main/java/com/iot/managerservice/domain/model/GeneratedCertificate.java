package com.iot.managerservice.domain.model;

// Contiene las cadenas en formato PEM listas para guardar en archivo
public record GeneratedCertificate(
        String privateKeyPem,
        String certificatePem
) {}