package com.iot.managerservice.usecase.crypto;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Caso de Uso para la administración operativa y auditoría de los certificados emitidos.
 * <p>
 * Encapsula la lógica de negocio para listar el estado del material criptográfico
 * y revocar el acceso a dispositivos comprometidos o dados de baja.
 * </p>
 */
@Service
public class ManageCertificatesUseCase {

    private final CertificateRepository repository;

    public ManageCertificatesUseCase(CertificateRepository repository) {
        this.repository = repository;
    }

    public List<CertificateData> getAll() {
        return repository.findAll();
    }

    /**
     * Revoca lógicamente un certificado dentro de la red.
     * <p>
     * <b>Nota de auditoría:</b> Este proceso no elimina físicamente el registro criptográfico
     * de la base de datos (por motivos de trazabilidad), sino que altera su estado
     * interno a "REVOKED", impidiendo su validación en futuras conexiones mTLS.
     * </p>
     *
     * @param id Identificador único (UUID) del certificado a revocar.
     */
    public void revoke(String id) {
        repository.updateStatus(id, "REVOKED");
    }
}
