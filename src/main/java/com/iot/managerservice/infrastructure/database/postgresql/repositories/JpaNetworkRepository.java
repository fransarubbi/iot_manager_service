package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.NetworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * Repositorio Spring Data JPA destinado a las operaciones sobre la entidad {@link NetworkEntity}.
 * <p>
 * Actúa como la interfaz de acceso a datos para la tabla {@code networks}, facilitando
 * la recuperación de las agrupaciones de red definidas en el sistema.
 * </p>
 */
@Repository
public interface JpaNetworkRepository extends JpaRepository<NetworkEntity, String> {

    /**
     * Recupera todas las redes lógicas que están bajo la administración de un
     * dispositivo Edge en particular.
     *
     * @param edgeId El identificador del dispositivo Edge controlador.
     * @return Lista de entidades de red asignadas al Edge especificado.
     */
    List<NetworkEntity> findByEdgeId(String edgeId);
}