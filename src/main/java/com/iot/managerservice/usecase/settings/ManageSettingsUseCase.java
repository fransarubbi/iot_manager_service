package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.port.GrpcMessageSender;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.infrastructure.cache.HubVersionCache;
import com.iot.managerservice.infrastructure.cache.HubVersionCache.CacheResult;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service  // Esto le dice a Spring que haga un Singleton
public class ManageSettingsUseCase {

    private final HubVersionCache ramCache;
    private final HubRepository database;
    private final GrpcMessageSender messageSender;

    public ManageSettingsUseCase(HubVersionCache ramCache, HubRepository database, GrpcMessageSender messageSender) {
        this.ramCache = ramCache;
        this.database = database;
        this.messageSender = messageSender;
    }

    public void execute(String hubId, Long incomingMsgId, Object payload) {

        CacheResult status = ramCache.checkAndUpdate(hubId, incomingMsgId);

        switch (status) {
            case NEW_HUB:
                try {
                    database.save(hubId, incomingMsgId, payload);
                    messageSender.sendAck(hubId);
                } catch (Exception e) {
                    // Si falla la BD, hay que deshacer el candado en RAM
                    ramCache.remove(hubId);
                    log.error("Fallo BD al guardar nuevo Hub.");
                }
                break;

            case VALID_UPDATE:
                try {
                    database.update(hubId, incomingMsgId, payload);
                    messageSender.sendAck(hubId);
                } catch (Exception e) {
                    log.error("Fallo BD al actualizar Hub.");
                    // Implementar un mecanismo para retroceder el ID en RAM
                }
                break;

            case DUPLICATE_MESSAGE:
                // Mensaje repetido = Mandar ACK sin tocar DB
                messageSender.sendAck(hubId);
                break;

            case OUTDATED_MESSAGE:
                log.warn("Mensaje viejo descartado.");
                break;
        }
    }
}