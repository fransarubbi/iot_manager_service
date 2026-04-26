package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.CertificateData;
import java.util.List;

/**
 * Puerto de persistencia para la gestión del ciclo de vida de los certificados.
 * <p>
 * Sigue el patrón Repository de Domain-Driven Design para abstraer el almacenamiento
 * de la base de datos subyacente y permitir la consulta y actualización del
 * historial criptográfico de la red.
 * </p>
 */
public interface CertificateRepository {

    /**
     * Guarda un nuevo registro de certificado o actualiza toda la información de uno existente.
     *
     * @param certificate Entidad de dominio con los datos del certificado a persistir.
     */
    void save(CertificateData certificate);

    /**
     * Recupera el registro completo de todos los certificados gestionados por el sistema.
     *
     * @return Una lista inmutable o colección de objetos {@link CertificateData}.
     */
    List<CertificateData> findAll();

    /**
     * Modifica exclusivamente el estado operativo de un certificado específico
     * dentro del sistema (por ejemplo, para marcarlo como revocado o expirado).
     *
     * @param id     El identificador único (UUID) del certificado.
     * @param status El nuevo estado a asignar (ej. "VALID", "REVOKED", "EXPIRED").
     */
    void updateStatus(String id, String status);
}