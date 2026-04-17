package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.model.HubSettings; // <-- 1. Importa el modelo de dominio
import com.iot.managerservice.domain.port.GrpcMessageSender;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.infrastructure.cache.HubVersionCache;
import com.iot.managerservice.infrastructure.cache.HubVersionCache.CacheResult;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ManageSettingsUseCase {

    private final HubVersionCache ramCache;
    private final HubRepository database;
    private final GrpcMessageSender messageSender;

    public ManageSettingsUseCase(HubVersionCache ramCache, HubRepository database, GrpcMessageSender messageSender) {
        this.ramCache = ramCache;
        this.database = database;
        this.messageSender = messageSender;
    }

    public void execute(HubSettings hub, Long incomingMsgId) {

        CacheResult status = ramCache.checkAndUpdate(hub.hubId(), incomingMsgId);

        switch (status) {
            case NEW_HUB:
                try {
                    database.save(hub, incomingMsgId);
                    messageSender.sendAck(hub, incomingMsgId);
                } catch (Exception e) {
                    ramCache.remove(hub.hubId());
                    log.error("Fallo BD al guardar nuevo Hub.");
                }
                break;

            case VALID_UPDATE:
                try {
                    database.update(hub, incomingMsgId);
                    messageSender.sendAck(hub, incomingMsgId);
                } catch (Exception e) {
                    log.error("Fallo BD al actualizar Hub.");
                }
                break;

            case DUPLICATE_MESSAGE:
                messageSender.sendAck(hub, incomingMsgId);
                break;

            case OUTDATED_MESSAGE:
                log.warn("Mensaje viejo descartado.");
                break;
        }
    }
}