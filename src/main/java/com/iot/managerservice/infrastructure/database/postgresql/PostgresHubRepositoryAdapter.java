package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.HubEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaHubRepository;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del puerto de persistencia {@link HubRepository} basada en PostgreSQL.
 * <p>
 * Este adaptador se encarga no solo de guardar la configuración de conectividad de los Hubs,
 * sino también de registrar y actualizar la marca de tiempo o ID del último mensaje
 * procesado, facilitando el control de concurrencia y el orden de los eventos gRPC.
 * </p>
 */
@Slf4j
@Repository
public class PostgresHubRepositoryAdapter implements HubRepository {

    private final JpaHubRepository jpaHubRepository;

    public PostgresHubRepositoryAdapter(JpaHubRepository jpaHubRepository) {
        this.jpaHubRepository = jpaHubRepository;
    }

    @Override
    public void save(HubSettings settings, Long messageId) {
        HubEntity entity = new HubEntity();
        entity.setHubId(settings.hubId());
        entity.setMessageId(messageId);
        mapSettingsToEntity(settings, entity); // Método auxiliar abajo

        jpaHubRepository.save(entity);
        log.info("Nuevo Hub guardado en Postgres: {}", settings.hubId());
    }

    @Override
    public void update(HubSettings settings, Long messageId) {
        jpaHubRepository.findById(settings.hubId()).ifPresent(entity -> {
            entity.setMessageId(messageId);
            mapSettingsToEntity(settings, entity);
            jpaHubRepository.save(entity);
            log.info("Hub actualizado en Postgres: {}", settings.hubId());
        });
    }

    private void mapSettingsToEntity(HubSettings s, HubEntity e) {
        e.setNetworkId(s.networkId());
        e.setDeviceName(s.deviceName());
        e.setWifiSsid(s.wifiSsid());
        e.setWifiPassword(s.wifiPassword());
        e.setMqttUri(s.mqttUri());
        e.setSample(s.sample());
        e.setEnergyMode(s.energyMode());
    }

    @Override
    public List<HubIdAndVersion> getAllHubVersions() {
        return jpaHubRepository.findAll().stream()
                .map(h -> new HubIdAndVersion(h.getHubId(), h.getMessageId()))
                .toList();
    }

    @Override
    public List<HubSettings> findByNetworkId(String networkId) {
        return jpaHubRepository.findByNetworkId(networkId).stream()
                .map(e -> new HubSettings(
                        e.getHubId(), e.getNetworkId(), e.getDeviceName(),
                        e.getWifiSsid(), e.getWifiPassword(), e.getMqttUri(), e.getSample(),
                        e.getEnergyMode()
                )).collect(Collectors.toList());
    }

    @Override
    public Optional<HubSettings> findById(String hubId) {
        return jpaHubRepository.findById(hubId)
                .map(e -> new HubSettings(e.getHubId(), e.getNetworkId(), e.getDeviceName(),
                        e.getWifiSsid(), e.getWifiPassword(), e.getMqttUri(), e.getSample(),
                        e.getEnergyMode()));
    }

    @Override
    public long countByNetworkId(String networkId) {
        return jpaHubRepository.countByNetworkId(networkId);
    }
}