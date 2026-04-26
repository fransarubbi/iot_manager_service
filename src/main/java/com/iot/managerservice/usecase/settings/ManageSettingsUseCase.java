package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.model.HubSettings; // <-- 1. Importa el modelo de dominio
import com.iot.managerservice.domain.port.GrpcMessageSender;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.domain.repository.NetworkRepository;
import com.iot.managerservice.infrastructure.cache.HubVersionCache;
import com.iot.managerservice.infrastructure.cache.HubVersionCache.CacheResult;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


/**
 * Caso de Uso crítico para el procesamiento de estado proveniente de los Hubs.
 * <p>
 * Maneja los flujos de datos ascendentes (del hardware al servidor). Utiliza un mecanismo
 * de caché atómico ({@link com.iot.managerservice.infrastructure.cache.HubVersionCache})
 * para garantizar la idempotencia de las operaciones, evaluando si el mensaje entrante es
 * nuevo, una actualización válida, un duplicado o está obsoleto.
 * Dependiendo del resultado, actualiza la base de datos y responde al hardware con un
 * Acuse de Recibo (ACK).
 * </p>
 */
@Slf4j
@Service
public class ManageSettingsUseCase {

    private final HubVersionCache ramCache;
    private final HubRepository database;
    private final GrpcMessageSender messageSender;
    private final NetworkRepository networkRepository;

    public ManageSettingsUseCase(HubVersionCache ramCache, HubRepository database,
                                 GrpcMessageSender messageSender, NetworkRepository networkRepository) {
        this.ramCache = ramCache;
        this.database = database;
        this.messageSender = messageSender;
        this.networkRepository = networkRepository;
    }

    public void execute(HubSettings hub, Long incomingMsgId) {

        String edgeId = networkRepository.findById(hub.networkId())
                .orElseThrow(() -> new IllegalArgumentException("Red no encontrada para enviar el ACK"))
                .edgeId();

        CacheResult status = ramCache.checkAndUpdate(hub.hubId(), incomingMsgId);

        switch (status) {
            case NEW_HUB:
                try {
                    database.save(hub, incomingMsgId);
                    messageSender.sendAck(edgeId, hub, incomingMsgId);
                } catch (Exception e) {
                    ramCache.remove(hub.hubId());
                    log.error("Fallo BD al guardar nuevo Hub.");
                }
                break;

            case VALID_UPDATE:
                try {
                    database.update(hub, incomingMsgId);
                    messageSender.sendAck(edgeId, hub, incomingMsgId);
                } catch (Exception e) {
                    log.error("Fallo BD al actualizar Hub.");
                }
                break;

            case DUPLICATE_MESSAGE:
                messageSender.sendAck(edgeId, hub, incomingMsgId);
                break;

            case OUTDATED_MESSAGE:
                log.warn("Mensaje viejo descartado.");
                break;
        }
    }
}