package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.CertificateRequest;
import com.iot.managerservice.domain.model.GeneratedCertificate;

/**
 * Puerto de salida para la creación de material criptográfico.
 * <p>
 * Define el contrato que el dominio utiliza para solicitar la generación de claves
 * y la emisión de certificados X.509 firmados por la Autoridad Certificante (CA)
 * interna del Manager, habilitando la seguridad mTLS en la red.
 * </p>
 */
public interface CertificateFactory {

    /**
     * Genera un nuevo par de claves criptográficas y emite un certificado firmado
     * basándose en las especificaciones provistas.
     *
     * @param request Objeto que contiene los parámetros de identidad y validez (Common Name, Device Type, etc.) para el nuevo certificado.
     * @return Un objeto {@link GeneratedCertificate} que encapsula las cadenas de la clave privada y el certificado público en formato PEM.
     */
    GeneratedCertificate createCertificate(CertificateRequest request);
}