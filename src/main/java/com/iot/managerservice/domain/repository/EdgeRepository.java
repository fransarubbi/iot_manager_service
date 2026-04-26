package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.Edge;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para el manejo de las entidades que representan a los dispositivos Edge.
 * <p>
 * Abstrae las operaciones CRUD (Crear, Leer, Actualizar, Borrar) asociadas a
 * la parametrización de red y comportamiento de los nodos principales.
 * </p>
 */
public interface EdgeRepository {

    /**
     * Guarda un nuevo dispositivo Edge en el repositorio o actualiza sus propiedades si ya existe.
     *
     * @param edge El objeto de dominio con la configuración del dispositivo.
     */
    void save(Edge edge);

    /**
     * Busca y recupera la configuración de un dispositivo Edge mediante su identificador.
     *
     * @param edgeId El identificador lógico único del Edge a buscar.
     * @return Un {@link Optional} que contiene el Edge si es encontrado, o vacío en caso contrario.
     */
    Optional<Edge> findById(String edgeId);

    /**
     * Obtiene una lista con todos los dispositivos Edge registrados actualmente en la infraestructura.
     *
     * @return Colección de entidades {@link Edge}.
     */
    List<Edge> findAll();

    /**
     * Elimina de forma permanente un dispositivo Edge y todos sus metadatos del repositorio.
     *
     * @param edgeId El identificador lógico del Edge a eliminar.
     */
    void deleteById(String edgeId);
}