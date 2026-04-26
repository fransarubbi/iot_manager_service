package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.model.HubSettings;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Componente de infraestructura que actúa como una caché en memoria temporal
 * para almacenar las configuraciones de los Hubs que se encuentran "en tránsito".
 * <p>
 * Dado que la comunicación hacia los dispositivos Edge vía gRPC es asíncrona,
 * esta clase retiene de forma segura los ajustes enviados hasta que el Edge
 * responda con un acuse de recibo (ACK) confirmando su correcta aplicación.
 * Cuenta con un mecanismo de expiración (TTL) para evitar fugas de memoria
 * en caso de que un mensaje se pierda en la red.
 * </p>
 */
@Component
public class PendingSettingsCache {

    private final Map<String, PendingSetting> cache = new ConcurrentHashMap<>();
    private record PendingSetting(HubSettings settings, long messageId, long expiresAt) {}

    /**
     * Almacena una nueva configuración en la caché asignándole un tiempo de vida (TTL) de 5 minutos.
     *
     * @param hubId     El identificador del Hub destino.
     * @param settings  Los nuevos parámetros a aplicar.
     * @param messageId El identificador de correlación para el seguimiento asíncrono.
     */
    public void put(String hubId, HubSettings settings, long messageId) {
        long expirationTime = System.currentTimeMillis() + 300_000;
        cache.put(hubId, new PendingSetting(settings, messageId, expirationTime));
    }

    /**
     * Intenta recuperar una configuración pendiente si el mensaje entrante coincide
     * y el tiempo de vida (TTL) aún no ha expirado. Si la recuperación es exitosa,
     * la entrada es removida de la caché para liberar memoria.
     *
     * @param hubId         El identificador del Hub del cual se recibe el ACK.
     * @param incomingMsgId El ID de correlación recibido del Edge.
     * @return Un {@link Optional} con la configuración si es válida; vacío si expiró o no coincide.
     */
    public Optional<HubSettings> getAndRemoveIfValid(String hubId, long incomingMsgId) {
        PendingSetting pending = cache.get(hubId);

        if (pending == null) return Optional.empty();

        if (System.currentTimeMillis() > pending.expiresAt()) {
            cache.remove(hubId);
            return Optional.empty();
        }

        if (pending.messageId() == incomingMsgId) {
            cache.remove(hubId);
            return Optional.of(pending.settings());
        }

        return Optional.empty();
    }
}
