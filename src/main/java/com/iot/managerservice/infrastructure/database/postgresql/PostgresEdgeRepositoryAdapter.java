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


@Slf4j
@Repository
public class PostgresEdgeRepositoryAdapter implements EdgeRepository {

    private final JpaEdgeRepository jpaRepository;

    public PostgresEdgeRepositoryAdapter(JpaEdgeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

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

    @Override
    public Optional<Edge> findById(String edgeId) {
        return jpaRepository.findById(edgeId).map(this::mapToDomain);
    }

    @Override
    public List<Edge> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String edgeId) {
        jpaRepository.deleteById(edgeId);
        log.info("Edge eliminado de la base de datos: {}", edgeId);
    }

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