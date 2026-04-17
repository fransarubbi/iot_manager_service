package com.iot.managerservice.domain.model;

public record CertificateRequest(
        String displayName,    // Ej: Certificado del edge de la sala8
        DeviceType deviceType, // EDGE, HUB, ROUTER
        String commonName,     // Ej: "edge-01"
        String organization,   // Ej: "Universidad"
        String country,        // Ej: "AR"
        String sanDomain,      // Ej: "edge-01.local" o "router.midominio.com"
        int validityDays       // Ej: 365
) {}