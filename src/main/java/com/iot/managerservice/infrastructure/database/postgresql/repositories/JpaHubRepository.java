package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.HubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * Repositorio Spring Data JPA encargado de las operaciones de base de datos para la entidad {@link HubEntity}.
 * <p>
 * Administra la tabla {@code hubs} en PostgreSQL. Incluye consultas personalizadas
 * mediante "Query Methods" para filtrar registros basándose en la topología lógica
 * de la red IoT.
 * </p>
 */
@Repository
public interface JpaHubRepository extends JpaRepository<HubEntity, String> {

    /**
     * Busca y devuelve todos los dispositivos Hub que están asociados a una red lógica específica.
     *
     * @param networkId El identificador de la red de la cual se quieren extraer los Hubs.
     * @return Una colección de las entidades Hub pertenecientes a dicha red.
     */
    List<HubEntity> findByNetworkId(String networkId);

    /**
     * Cuenta eficientemente la cantidad de registros de Hubs existentes para una red dada.
     * <p>
     * Se traduce en una consulta {@code SELECT COUNT(h) FROM hubs h WHERE h.network_id = ?},
     * evitando cargar los objetos en memoria para contarlos en Java.
     * </p>
     *
     * @param networkId El identificador de la red a evaluar.
     * @return El número total de Hubs bajo esa red.
     */
    long countByNetworkId(String networkId);
}