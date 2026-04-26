package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.port.GrpcMessageSender;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.domain.repository.NetworkRepository;
import com.iot.managerservice.infrastructure.cache.PendingSettingsCache;
import org.springframework.stereotype.Service;

/**
 * Caso de Uso responsable de iniciar la actualización asíncrona de configuraciones en los dispositivos Hub.
 * <p>
 * Este servicio de la capa de aplicación orquesta la preparación y el despacho de nuevos
 * parámetros operativos (como credenciales Wi-Fi o tiempos de muestreo) hacia el hardware.
 * Debido a la naturaleza eventual de la red gRPC, este proceso no aplica los cambios
 * directamente en la base de datos; en su lugar, calcula el siguiente identificador de mensaje válido,
 * deposita la configuración en una memoria caché de "tránsito" ({@link PendingSettingsCache})
 * y delega el envío físico al puerto de comunicación gRPC.
 * </p>
 */
@Service
public class SendNewHubSettingsUseCase {

    private final HubRepository hubRepository;
    private final NetworkRepository networkRepository;
    private final PendingSettingsCache pendingCache;
    private final GrpcMessageSender grpcSender;

    public SendNewHubSettingsUseCase(HubRepository hubRepository, NetworkRepository networkRepository,
                                     PendingSettingsCache pendingCache, GrpcMessageSender grpcSender) {
        this.hubRepository = hubRepository;
        this.networkRepository = networkRepository;
        this.pendingCache = pendingCache;
        this.grpcSender = grpcSender;
    }

    /**
     * Ejecuta el flujo de envío de una nueva parametrización hacia un Hub.
     * <ol>
     * <li>Valida la existencia del Hub en el sistema.</li>
     * <li>Calcula el {@code newMsgId} incrementando en 1 la versión del último mensaje conocido, garantizando el orden y la idempotencia.</li>
     * <li>Almacena la intención de cambio en la caché temporal a la espera del Acuse de Recibo (ACK) del dispositivo.</li>
     * <li>Enruta el comando a través del dispositivo Edge administrador de la red a la que pertenece el Hub.</li>
     * </ol>
     *
     * @param hubId       El identificador lógico del Hub que recibirá los nuevos ajustes.
     * @param newSettings El modelo de dominio que contiene los nuevos parámetros a aplicar.
     * @throws IllegalArgumentException Si el Hub solicitado o la red a la que pertenece no se encuentran registrados en el sistema.
     */
    public void execute(String hubId, HubSettings newSettings) {
        HubSettings currentHub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("Hub no encontrado"));

        long currentMsgId = hubRepository.getAllHubVersions().stream()
                .filter(h -> h.hubId().equals(hubId))
                .findFirst()
                .map(HubIdAndVersion::lastMessageId)
                .orElse(0L);

        long newMsgId = currentMsgId + 1;

        HubSettings settingsToSend = new HubSettings(
                hubId,
                newSettings.networkId(),
                newSettings.deviceName(),
                newSettings.wifiSsid(),
                newSettings.wifiPassword(),
                newSettings.mqttUri(),
                newSettings.sample(),
                newSettings.energyMode()
        );

        pendingCache.put(hubId, settingsToSend, newMsgId);

        String edgeId = networkRepository.findById(currentHub.networkId())
                .orElseThrow(() -> new IllegalArgumentException("Red no encontrada")).edgeId();

        grpcSender.sendHubSettings(edgeId, newMsgId, settingsToSend);
    }
}