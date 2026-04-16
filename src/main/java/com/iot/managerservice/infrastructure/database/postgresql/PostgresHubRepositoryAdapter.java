package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.application.grpc.generated.Settings; // IMPORTANTE: Importamos tu clase gRPC
import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.HubEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaHubRepository;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class PostgresHubRepositoryAdapter implements HubRepository {

    private final JpaHubRepository jpaHubRepository;

    public PostgresHubRepositoryAdapter(JpaHubRepository jpaHubRepository) {
        this.jpaHubRepository = jpaHubRepository;
    }

    @Override
    public void save(String hubId, Long messageId, Object payload) {
        Settings settings = (Settings) payload;

        HubEntity entity = new HubEntity();
        entity.setHubId(hubId);
        entity.setMessageId(messageId);
        entity.setNetworkId(settings.getNetwork());
        entity.setDeviceName(settings.getDeviceName());
        entity.setWifiSsid(settings.getWifiSsid());
        entity.setWifiPassword(settings.getWifiPassword());
        entity.setMqttUri(settings.getMqttUri());
        entity.setSample(settings.getSample());

        entity.setEnergyMode(settings.getEnergyMode());

        jpaHubRepository.save(entity);
        log.info("Guardado nuevo registro en Postgres para el Hub: {} con MsgId: {}", hubId, messageId);
    }

    @Override
    public void update(String hubId, Long messageId, Object payload) {
        Settings settings = (Settings) payload;

        jpaHubRepository.findById(hubId).ifPresentOrElse(entity -> {
            // Si lo encuentra, actualizar campos
            entity.setMessageId(messageId);
            entity.setNetworkId(settings.getNetwork());
            entity.setDeviceName(settings.getDeviceName());
            entity.setWifiSsid(settings.getWifiSsid());
            entity.setWifiPassword(settings.getWifiPassword());
            entity.setMqttUri(settings.getMqttUri());
            entity.setSample(settings.getSample());
            entity.setEnergyMode(settings.getEnergyMode());

            jpaHubRepository.save(entity);
            log.info("Actualizado registro existente en Postgres para el Hub: {} con MsgId: {}", hubId, messageId);
        }, () -> {
            log.error("Intento de actualizar un Hub que no existe en BD: {}", hubId);
        });
    }

    @Override
    public List<HubIdAndVersion> getAllHubVersions() {
        log.info("Consultando todas las versiones de Hubs en la base de datos...");

        return jpaHubRepository.findAll().stream()
                .map(entity -> new HubIdAndVersion(entity.getHubId(), entity.getMessageId()))
                .collect(Collectors.toList());
    }
}