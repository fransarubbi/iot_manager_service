package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.Network;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para la gestión de Redes Lógicas.
 * <p>
 * Abstrae el almacenamiento de las agrupaciones de Hubs (Redes) que
 * son administradas por los dispositivos Edge.
 * </p>
 */
public interface NetworkRepository {

    /**
     * Guarda una nueva red o actualiza sus propiedades si ya existe.
     *
     * @param network La entidad de dominio que representa la red.
     */
    void save(Network network);

    /**
     * Busca una red específica por su identificador.
     *
     * @param networkId Identificador único de la red.
     * @return Un {@link Optional} con la red si fue encontrada.
     */
    Optional<Network> findById(String networkId);

    /**
     * Obtiene el listado completo de todas las redes configuradas en el sistema.
     *
     * @return Lista de entidades {@link Network}.
     */
    List<Network> findAll();

    /**
     * Elimina una red lógica del repositorio.
     *
     * @param networkId Identificador de la red a eliminar.
     */
    void deleteById(String networkId);

    /**
     * Modifica el estado operativo (activa/inactiva) de una red sin alterar
     * el resto de sus propiedades.
     *
     * @param networkId Identificador de la red.
     * @param active    {@code true} para activar la red, {@code false} para desactivarla.
     */
    void updateActiveStatus(String networkId, boolean active);

    /**
     * Recupera todas las redes que están bajo la administración de un Edge específico.
     *
     * @param edgeId Identificador del dispositivo Edge controlador.
     * @return Lista de redes administradas por dicho Edge.
     */
    List<Network> findByEdgeId(String edgeId);
}