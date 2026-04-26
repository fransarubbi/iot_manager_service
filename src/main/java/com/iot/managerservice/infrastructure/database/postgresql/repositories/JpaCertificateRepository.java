package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link CertificateEntity}.
 * <p>
 * Se encarga de la comunicación directa con la tabla {@code certificates} en PostgreSQL.
 * Spring Boot proporciona automáticamente la implementación de esta interfaz en tiempo de ejecución.
 * Es utilizado por el adaptador de infraestructura para cumplir con el contrato del dominio.
 * </p>
 */
@Repository
public interface JpaCertificateRepository extends JpaRepository<CertificateEntity, String> {

    /**
     * Recupera una lista de certificados filtrados por su estado operativo actual.
     * <p>
     * Se trata de un "Query Method" de Spring Data, donde la consulta SQL es generada
     * automáticamente a partir de la firma del método.
     * </p>
     *
     * @param status El estado de los certificados que se desean buscar (ej. "VALID", "REVOKED").
     * @return Una lista de entidades que coinciden con el estado solicitado.
     */
    List<CertificateEntity> findByStatus(String status);
}