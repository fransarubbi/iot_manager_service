package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.CertificateData;
import com.iot.managerservice.domain.repository.CertificateRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.CertificateEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaCertificateRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Adaptador de infraestructura para la persistencia de certificados criptográficos en PostgreSQL.
 * <p>
 * Implementa el puerto de salida {@link CertificateRepository} definido en la capa de dominio.
 * Su responsabilidad es traducir las entidades del dominio ({@link CertificateData}) hacia
 * las entidades JPA ({@link CertificateEntity}) y delegar las operaciones de base de datos
 * al repositorio de Spring Data JPA correspondiente.
 * </p>
 */
@Repository
public class PostgresCertificateRepositoryAdapter implements CertificateRepository {

    private final JpaCertificateRepository jpaRepository;

    /**
     * Construye el adaptador inyectando la dependencia del repositorio de Spring Data JPA.
     *
     * @param jpaRepository Repositorio JPA concreto proporcionado por Spring Boot.
     */
    public PostgresCertificateRepositoryAdapter(JpaCertificateRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Persiste un nuevo certificado en la base de datos.
     * <p>Realiza el mapeo directo de los campos del registro de dominio hacia la entidad JPA.</p>
     *
     * @param c El objeto de dominio que representa los datos del certificado.
     */
    @Override
    public void save(CertificateData c) {
        CertificateEntity entity = new CertificateEntity(
                c.id(), c.displayName(), c.deviceType(), c.status(),
                c.emissionDate(), c.expirationDate(), c.privateKeyPem(), c.certificatePem()
        );
        jpaRepository.save(entity);
    }

    /**
     * Recupera exclusivamente los certificados que se encuentran en estado operativo válido.
     *
     * @return Lista de certificados mapeados de vuelta al modelo de dominio.
     */
    @Override
    public List<CertificateData> findAll() {
        return jpaRepository.findByStatus("VALID").stream()
                .map(e -> new CertificateData(e.getId(), e.getDisplayName(),
                        e.getDeviceType(), e.getStatus(), e.getEmissionDate(),
                        e.getExpirationDate(), e.getPrivateKeyPem(), e.getCertificatePem()))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un certificado existente sin alterar su material criptográfico.
     *
     * @param id     El identificador único (UUID) del certificado.
     * @param status El nuevo estado a registrar (ej. "REVOKED").
     */
    @Override
    public void updateStatus(String id, String status) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.setStatus(status);
            jpaRepository.save(e);
        });
    }
}
