package com.iot.managerservice.infrastructure.database.postgresql;


import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.repository.EdgeRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.EdgeEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaEdgeRepository;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Adaptador de persistencia concreto para la gestión de dispositivos Edge utilizando PostgreSQL.
 * <p>
 * Cumple con el contrato de {@link EdgeRepository}. Aísla completamente a los Casos de Uso
 * (capa de aplicación) de los detalles de implementación relacionales (tablas, columnas, JPA).
 * </p>
 */
@Slf4j
@Repository
public class PostgresEdgeRepositoryAdapter implements EdgeRepository {

    private final JpaEdgeRepository jpaRepository;

    public PostgresEdgeRepositoryAdapter(JpaEdgeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Guarda la configuración completa de un dispositivo Edge.
     * Convierte el modelo inmutable del dominio hacia una entidad JPA mutable antes de persistir.
     *
     * @param edge Objeto de dominio con los parámetros de configuración.
     */
    @Override
    public void save(Edge edge) {
        EdgeEntity entity = new EdgeEntity();

        entity.setEdgeId(edge.edgeId());
        entity.setName(edge.name());
        entity.setLocation(edge.location());
        entity.setCn(edge.cn());
        entity.setHostServer(edge.hostServer());
        entity.setHostPort(edge.hostPort());
        entity.setHostLocal(edge.hostLocal());
        entity.setDataBasePath(edge.dataBasePath());
        entity.setBufferLength(edge.bufferLength());
        entity.setLogLevel(edge.logLevel());
        entity.setMaxNumberHandshakeAttempts(edge.maxNumberHandshakeAttempts());
        entity.setFrequencyMessagesPhase(edge.frequencyMessagesPhase());
        entity.setFrequencyMessagesSafeMode(edge.frequencyMessagesSafeMode());
        entity.setHandshakeTimeLimit(edge.handshakeTimeLimit());
        entity.setPhaseTimeLimit(edge.phaseTimeLimit());
        entity.setSafeModeTimeLimit(edge.safeModeTimeLimit());
        entity.setHeartbeatBalanceModeTime(edge.heartbeatBalanceModeTime());
        entity.setHeartbeatNormalTime(edge.heartbeatNormalTime());
        entity.setHeartbeatSafeModeTime(edge.heartbeatSafeModeTime());

        jpaRepository.save(entity);
        log.info("Edge guardado/actualizado en base de datos: {}", edge.edgeId());
    }

    /**
     * Busca la configuración de un Edge específico y la traduce al lenguaje del dominio.
     *
     * @param edgeId El identificador lógico único del Edge.
     * @return Un {@link Optional} que contiene la instancia del dominio si fue encontrada.
     */
    @Override
    public Optional<Edge> findById(String edgeId) {
        return jpaRepository.findById(edgeId).map(this::mapToDomain);
    }

    /**
     * Obtiene el catálogo completo de dispositivos Edge registrados, mapeándolos al dominio.
     *
     * @return Lista de entidades de dominio {@link Edge}.
     */
    @Override
    public List<Edge> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Elimina físicamente un registro de Edge de la base de datos subyacente.
     *
     * @param edgeId El identificador del Edge a remover.
     */
    @Override
    public void deleteById(String edgeId) {
        jpaRepository.deleteById(edgeId);
        log.info("Edge eliminado de la base de datos: {}", edgeId);
    }

    /**
     * Método auxiliar (Mapper interno) para transformar una entidad de infraestructura
     * hacia un objeto puro del dominio.
     *
     * @param entity La entidad recuperada de PostgreSQL.
     * @return La representación en el modelo de dominio.
     */
    private Edge mapToDomain(EdgeEntity entity) {
        return new Edge(
                entity.getEdgeId(), entity.getName(), entity.getLocation(),
                entity.getCn(), entity.getHostServer(), entity.getHostPort(),
                entity.getHostLocal(), entity.getDataBasePath(), entity.getBufferLength(),
                entity.getLogLevel(), entity.getMaxNumberHandshakeAttempts(),
                entity.getFrequencyMessagesPhase(), entity.getFrequencyMessagesSafeMode(),
                entity.getHandshakeTimeLimit(), entity.getPhaseTimeLimit(),
                entity.getSafeModeTimeLimit(), entity.getHeartbeatBalanceModeTime(),
                entity.getHeartbeatNormalTime(), entity.getHeartbeatSafeModeTime()
        );
    }
}