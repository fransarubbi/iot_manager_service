package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.repository.HubRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Componente de infraestructura para gestionar la idempotencia y el orden de los
 * mensajes provenientes de los Hubs en memoria RAM.
 * <p>
 * Implementa mecanismos atómicos para evitar condiciones de carrera (Race Conditions)
 * cuando múltiples hilos de gRPC intentan actualizar el estado de un mismo Hub simultáneamente.
 * </p>
 */
@Component
public class HubVersionCache {

    /** Mapa concurrente que asocia el ID de un Hub con la versión (ID) de su último mensaje procesado. */
    private final ConcurrentHashMap<String, Long> versions = new ConcurrentHashMap<>();
    private final HubRepository hubRepository;

    public HubVersionCache(HubRepository hubRepository) {
        this.hubRepository = hubRepository;
    }

    /**
     * Carga el estado inicial de las versiones desde la persistencia hacia la RAM
     * durante el arranque de la aplicación.
     */
    @PostConstruct
    public void loadInitialDataFromDatabase() {
        hubRepository.getAllHubVersions().forEach(hub -> {
            versions.put(hub.hubId(), hub.lastMessageId());
        });
    }

    /**
     * Evalúa y actualiza atómicamente la versión del mensaje de un Hub.
     * <p>
     * Si dos hilos entran a la vez para el mismo {@code hubId}, se garantiza el bloqueo a nivel
     * de clave para evitar inconsistencias. Retorna una directiva para que la capa de Casos
     * de Uso sepa cómo proceder con el mensaje entrante.
     * </p>
     *
     * @param hubId         Identificador único del Hub emisor.
     * @param incomingMsgId ID secuencial o marca de tiempo del mensaje entrante a evaluar.
     * @return Un {@link CacheResult} indicando si el mensaje es nuevo, válido, duplicado u obsoleto.
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

    /**
     * Enumeración interna que clasifica el resultado de la evaluación atómica en la caché.
     */
    public enum CacheResult {
        /** El Hub es desconocido en la caché y este es su primer mensaje registrado. */
        NEW_HUB,
        /** El mensaje tiene un ID mayor al registrado; es una actualización válida y en orden. */
        VALID_UPDATE,
        /** El mensaje tiene exactamente el mismo ID que el último procesado (reintento/duplicado). */
        DUPLICATE_MESSAGE,
        /** El mensaje llegó tarde o fuera de orden y su ID es menor al registrado en caché. */
        OUTDATED_MESSAGE
    }

    /**
     * Elimina el registro de un Hub de la caché en memoria.
     *
     * @param hubId Identificador del Hub a purgar.
     */
    public void remove(String hubId) {
        versions.remove(hubId);
    }
}