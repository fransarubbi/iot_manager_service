package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.model.Network;

/**
 * Puerto de salida para la comunicación asíncrona hacia el Router.
 * <p>
 * Define el contrato para enviar comandos de control, actualizaciones de topología
 * y configuraciones operativas desde el Manager central hacia los Edges mediante gRPC.
 * </p>
 */
public interface GrpcMessageSender {

    /**
     * Envía un acuse de recibo (Acknowledgement) a un Hub confirmando que el mensaje
     * de configuración de un Hub ha sido procesado exitosamente por el Manager.
     *
     * @param edgeId    Identificador del Edge destino.
     * @param hub       La configuración o datos del Hub asociado al mensaje.
     * @param messageId Identificador secuencial del mensaje que está siendo reconocido.
     */
    void sendAck(String edgeId, HubSettings hub, Long messageId);

    /**
     * Notifica a un Edge sobre una alteración en la topología lógica de su red
     * (por ejemplo, la creación de una nueva red o la desactivación de una existente).
     *
     * @param network       La entidad de red afectada por el cambio.
     * @param operation     Tipo de operación realizada (ej. "CREATE", "UPDATE", "DELETE").
     * @param unixTimestamp Marca de tiempo del momento exacto en que se efectuó el cambio.
     */
    void sendNetworkUpdate(Network network, String operation, long unixTimestamp);

    /**
     * Transmite una nueva configuración operativa (credenciales Wi-Fi, parámetros MQTT, etc.)
     * hacia un Hub específico, enrutando el mensaje a través de su Edge administrador.
     *
     * @param edgeId    Identificador del Edge que actúa como intermediario hacia el Hub.
     * @param messageId Identificador de correlación para el seguimiento de esta transacción.
     * @param settings  La nueva parametrización a aplicar en el Hub.
     */
    void sendHubSettings(String edgeId, Long messageId, HubSettings settings);
}