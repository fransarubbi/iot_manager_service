package com.iot.managerservice.domain.model;

/**
 * Entidad de dominio que representa un certificado criptográfico gestionado por el sistema.
 * <p>
 * Almacena tanto la información operativa de identidad como el estado de validez
 * y el material criptográfico base. Se utiliza para consultar el historial de seguridad
 * y orquestar renovaciones de los dispositivos conectados.
 * </p>
 *
 * @param id             Identificador único universal (UUID) del registro del certificado.
 * @param displayName    Alias descriptivo proporcionado durante la solicitud inicial.
 * @param deviceType     Rol jerárquico del dispositivo propietario de estas credenciales.
 * @param status         Estado actual del ciclo de vida del certificado dentro del sistema (ej. "VALID", "REVOKED", "EXPIRED").
 * @param emissionDate   Fecha y hora de emisión del certificado expresada en milisegundos (Unix timestamp).
 * @param expirationDate Fecha y hora en la cual el certificado perderá su validez, en milisegundos (Unix timestamp).
 * @param privateKeyPem  Cadena con la clave privada en formato PEM. Se preserva para habilitar la descarga bajo demanda de paquetes de provisión.
 * @param certificatePem Cadena con la clave pública firmada en formato PEM.
 */
public record CertificateData(
        String id,
        String displayName,
        DeviceType deviceType,
        String status,
        long emissionDate,
        long expirationDate,
        String privateKeyPem,
        String certificatePem
) {}