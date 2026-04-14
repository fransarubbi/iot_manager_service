package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.repository.HubRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class HubVersionCache {

    private final ConcurrentHashMap<String, Long> versions = new ConcurrentHashMap<>();
    private final HubRepository hubRepository;

    public HubVersionCache(HubRepository hubRepository) {
        this.hubRepository = hubRepository;
    }

    @PostConstruct
    public void loadInitialDataFromDatabase() {
        // Carga los registros de Postgres a la RAM al iniciar
        hubRepository.getAllHubVersions().forEach(hub -> {
            versions.put(hub.hubId(), hub.lastMessageId());
        });
    }

    /**
     * Este método es atómico. Si dos hilos entran a la vez para el mismo hubId,
     * Java bloquea a uno por milisegundos mientras el otro termina la evaluación.
     * * Retorna un Enum para decirle al Caso de Uso exactamente qué hacer.
     */
    public CacheResult checkAndUpdate(String hubId, Long incomingMsgId) {

        final CacheResult[] result = new CacheResult[1];

        versions.compute(hubId, (key, currentId) -> {
            if (currentId == null) {
                result[0] = CacheResult.NEW_HUB;
                return incomingMsgId;
            }
            if (incomingMsgId > currentId) {
                result[0] = CacheResult.VALID_UPDATE;
                return incomingMsgId;
            }
            if (incomingMsgId.equals(currentId)) {
                result[0] = CacheResult.DUPLICATE_MESSAGE;
                return currentId;
            }
            result[0] = CacheResult.OUTDATED_MESSAGE;
            return currentId;
        });

        return result[0];
    }

    public enum CacheResult {
        NEW_HUB, VALID_UPDATE, DUPLICATE_MESSAGE, OUTDATED_MESSAGE
    }

    public void remove(String hubId) {
        versions.remove(hubId);
    }
}