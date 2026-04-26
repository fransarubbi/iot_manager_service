package com.iot.managerservice.domain.model;

/**
 * Representa una solicitud de dominio para la creación de un nuevo certificado criptográfico.
 * <p>
 * Contiene todos los parámetros necesarios para generar un certificado X.509
 * válido para la infraestructura de clave pública (PKI) de la red, incluyendo
 * la información de identidad del dispositivo y sus restricciones de validez.
 * </p>
 *
 * @param displayName  Nombre amigable para la identificación visual en interfaces (ej. "Certificado del edge de la sala 8").
 * @param deviceType   El rol o tipo de dispositivo al que se le emitirá el certificado (EDGE, HUB, ROUTER).
 * @param commonName   Nombre común (CN) que identifica de manera única al dispositivo dentro de la topología (ej. "edge-01").
 * @param organization Nombre de la entidad, proyecto u organización a la que pertenece el dispositivo.
 * @param country      Código de país de dos letras ISO 3166-1 alpha-2 correspondiente a la ubicación (ej. "AR").
 * @param sanDomain    Nombre de Dominio Alternativo (Subject Alternative Name) para la validación mTLS (ej. "edge-01.local").
 * @param validityDays Cantidad de días de validez activa desde el momento en que se emite el certificado.
 */
public record CertificateRequest(
        String displayName,
        DeviceType deviceType,
        String commonName,
        String organization,
        String country,
        String sanDomain,
        int validityDays
) {}