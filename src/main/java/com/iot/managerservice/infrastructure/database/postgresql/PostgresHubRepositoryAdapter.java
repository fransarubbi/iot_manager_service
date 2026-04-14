package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.repository.HubRepository;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;


@Slf4j
@Repository
public class PostgresHubRepositoryAdapter implements HubRepository {

    @Override
    public void save(String hubId, Long messageId, Object payload) {
        // Por ahora un log, luego poner el código de Spring Data JPA
        log.info("Guardando nuevo registro en Postgres para el Hub: {} con MsgId: {}", hubId, messageId);
    }

    @Override
    public void update(String hubId, Long messageId, Object payload) {
        // Por ahora un log
        log.info("Actualizando registro existente en Postgres para el Hub: {} con MsgId: {}", hubId, messageId);
    }

    @Override
    public List<HubIdAndVersion> getAllHubVersions() {
        log.info("Consultando todas las versiones de Hubs en la base de datos...");

        // Por ahora retorna una lista vacía
        // Más adelante se debe conectar esto con Spring Data JPA.
        return Collections.emptyList();
    }
}