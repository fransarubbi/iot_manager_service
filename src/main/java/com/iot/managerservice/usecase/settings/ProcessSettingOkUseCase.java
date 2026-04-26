package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.infrastructure.cache.HubVersionCache;
import com.iot.managerservice.infrastructure.cache.PendingSettingsCache;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de Uso para el cierre del ciclo de configuración asíncrona.
 * <p>
 * Cuando un administrador envía nuevos ajustes a un Hub, estos quedan en estado "pendiente".
 * Este servicio procesa la respuesta definitiva del microcontrolador (confirmando si logró
 * aplicar los cambios de Wi-Fi, MQTT, etc. o si falló). Si el cambio fue exitoso, persiste
 * los datos finales y actualiza las versiones; en cualquier caso, emite una notificación
 * al sistema de alertas operativas.
 * </p>
 */
@Slf4j
@Service
public class ProcessSettingOkUseCase {

    private final PendingSettingsCache pendingCache;
    private final HubRepository hubRepository;
    private final HubVersionCache hubVersionCache;
    private final ManageNotificationsUseCase notificationUseCase;

    public ProcessSettingOkUseCase(PendingSettingsCache pendingCache, HubRepository hubRepository,
                                   HubVersionCache hubVersionCache, ManageNotificationsUseCase notificationUseCase) {
        this.pendingCache = pendingCache;
        this.hubRepository = hubRepository;
        this.hubVersionCache = hubVersionCache;
        this.notificationUseCase = notificationUseCase;
    }

    public void execute(String hubId, long incomingMsgId, boolean result, String msg) {
        if (!result) {
            log.warn("El Hub {} rechazó la nueva configuración: {}", hubId, msg);
            notificationUseCase.createNotification("ERROR_CONFIG", "El hub: {} hardware rechazó la configuración." + hubId);
            return;
        }

        pendingCache.getAndRemoveIfValid(hubId, incomingMsgId).ifPresent(confirmedSettings -> {
            hubRepository.update(confirmedSettings, incomingMsgId);
            hubVersionCache.checkAndUpdate(hubId, incomingMsgId);
            log.info("Configuración {} aplicada con éxito en Hub {}", incomingMsgId, hubId);
            notificationUseCase.createNotification("SETTING_OK", "Nueva configuración aplicada al hub {}" + hubId);
        });
    }
}
