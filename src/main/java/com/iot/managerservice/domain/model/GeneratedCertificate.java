package com.iot.managerservice.domain.model;

/**
 * Representa el resultado directo de una operación de generación criptográfica.
 * <p>
 * Este registro es un objeto de transferencia interna (DTO de dominio) que encapsula
 * los artefactos en texto plano listos.
 * </p>
 *
 * @param privateKeyPem  La clave privada criptográfica generada para el dispositivo, codificada en formato PEM.
 * @param certificatePem El certificado público del dispositivo firmado por la CA del Manager, codificado en formato PEM.
 */
public record GeneratedCertificate(
        String privateKeyPem,
        String certificatePem
) {}