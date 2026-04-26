package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para la gestión de dispositivos Hub.
 * <p>
 * Define el contrato para almacenar la configuración de los Hubs
 * y mantener un registro de la versión del último mensaje recibido, garantizando
 * la sincronización y evitando el procesamiento de datos obsoletos.
 * </p>
 */
public interface HubRepository {

    /**
     * Persiste un nuevo dispositivo Hub junto con la versión de su primer mensaje.
     *
     * @param settings  La configuración inicial del Hub.
     * @param messageId El identificador secuencial del mensaje.
     */
    void save(HubSettings settings, Long messageId);

    /**
     * Actualiza la configuración de un Hub existente y el identificador de su último mensaje.
     *
     * @param settings  La nueva configuración a aplicar.
     * @param messageId El identificador secuencial del mensaje.
     */
    void update(HubSettings settings, Long messageId);

    /**
     * Recupera una proyección ligera de todos los Hubs registrados junto con la versión
     * de su último mensaje. Utilizado para precargar la caché en memoria al inicio.
     *
     * @return Una lista de objetos {@link HubIdAndVersion}.
     */
    List<HubIdAndVersion> getAllHubVersions();

    /**
     * Obtiene todos los Hubs que están asociados a una red lógica específica.
     *
     * @param networkId Identificador de la red.
     * @return Lista de configuraciones de los Hubs pertenecientes a la red.
     */
    List<HubSettings> findByNetworkId(String networkId);

    /**
     * Cuenta la cantidad total de Hubs registrados bajo una red específica.
     *
     * @param networkId Identificador de la red a evaluar.
     * @return El número total de Hubs en dicha red.
     */
    long countByNetworkId(String networkId);

    /**
     * Busca la configuración detallada de un Hub mediante su identificador único.
     *
     * @param hubId Identificador del Hub.
     * @return Un {@link Optional} con la configuración si existe, o vacío en caso contrario.
     */
    Optional<HubSettings> findById(String hubId);
}