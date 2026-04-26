package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.application.grpc.generated.*;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import com.iot.managerservice.usecase.settings.ManageSettingsUseCase;
import com.iot.managerservice.usecase.settings.ProcessSettingOkUseCase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador y Adaptador de Entrada (Driving Adapter) primario para el protocolo gRPC.
 * <p>
 * Actúa como el servidor central oyente de la red. Hereda de la base autogenerada por Protobuf
 * ({@code ManagerServiceImplBase}) para aceptar y mantener conexiones de streaming bidireccional
 * desde el Router. Su responsabilidad fundamental es interceptar los paquetes en crudo,
 * transformarlos al lenguaje de dominio y disparar los Casos de Uso pertinentes.
 * </p>
 */
@Slf4j
@GrpcService
public class GrpcController extends ManagerServiceGrpc.ManagerServiceImplBase {

    private final ManageSettingsUseCase manageSettingsUseCase;
    private final GrpcMessageSenderImpl messageSender;
    private final EdgeValidationCache edgeValidationCache;
    private final ManageNotificationsUseCase notificationUseCase;
    private final ProcessSettingOkUseCase processSettingOkUseCase;

    public GrpcController(ManageSettingsUseCase manageSettingsUseCase,
                          GrpcMessageSenderImpl messageSender,
                          EdgeValidationCache edgeValidationCache,
                          ManageNotificationsUseCase notificationUseCase,
                          ProcessSettingOkUseCase processSettingOkUseCase) {
        this.manageSettingsUseCase = manageSettingsUseCase;
        this.messageSender = messageSender;
        this.edgeValidationCache = edgeValidationCache;
        this.notificationUseCase = notificationUseCase;
        this.processSettingOkUseCase = processSettingOkUseCase;
    }

    @Override
    public StreamObserver<ToManager> connectStream(StreamObserver<FromManager> responseObserver) {

        log.info("¡El Router ha iniciado la conexión bidireccional!");
        messageSender.setRouterStream(responseObserver);

        return new StreamObserver<ToManager>() {

            @Override
            public void onNext(ToManager request) {
                String edgeId = request.getEdgeId();

                if (!edgeValidationCache.isValid(edgeId)) {
                    log.warn("ACCESO DENEGADO: Se recibió un mensaje de un edge_id no registrado ({}). Paquete descartado.", edgeId);
                    return;
                }

                switch (request.getPayloadCase()) {

                    case SETTINGS:
                        handleSettings(edgeId, request.getSettings());
                        break;

                    case SETTING_OK:
                        com.iot.managerservice.application.grpc.generated.SettingOk okMsg = request.getSettingOk();
                        String targetHubId = okMsg.getMetadata().getSenderUserId();
                        long msgIdToConfirm = okMsg.getMessageId();
                        boolean result = true;
                        String hardwareMsg = "Configuración aplicada en hardware";
                        log.info("Recibido SettingOk desde el Hub: {}", targetHubId);
                        processSettingOkUseCase.execute(targetHubId, msgIdToConfirm, result, hardwareMsg);
                        break;

                    case HELLO_WORLD: {
                        String description = "edge_id: " + request.getHelloWorld().getMetadata().getSenderUserId()
                                + "timestamp: " + request.getHelloWorld().getMetadata().getTimestamp();
                        notificationUseCase.createNotification("HELLO_WORLD", description);
                        break;
                    }

                    case FIRMWARE_OUTCOME: {
                        String description = "edge_id: " + request.getFirmwareOutcome().getMetadata().getSenderUserId()
                                + "timestamp: " + request.getFirmwareOutcome().getMetadata().getTimestamp()
                                + "network_id: " + request.getFirmwareOutcome().getNetwork()
                                + "is_ok: " + request.getFirmwareOutcome().getIsOk()
                                + "percentage: " + request.getFirmwareOutcome().getPercentageOk();
                        notificationUseCase.createNotification("FIRMWARE_OUTCOME", description);
                        break;
                    }

                    case PAYLOAD_NOT_SET:
                        log.warn("Se recibió un paquete vacío desde el Edge: {}", edgeId);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("El stream con el Router se rompió: {}", t.getMessage());
                // Por seguridad, desconectamos
                messageSender.setRouterStream(null);
            }

            @Override
            public void onCompleted() {
                log.info("El Router cerró la conexión.");
                messageSender.setRouterStream(null);
                responseObserver.onCompleted();
            }

            private void handleSettings(String edgeId, Settings g) {
                Long messageId = g.getMessageId();

                HubSettings domainSettings = new HubSettings(
                        g.getMetadata().getSenderUserId(),
                        g.getNetwork(),
                        g.getDeviceName(),
                        g.getWifiSsid(),
                        g.getWifiPassword(),
                        g.getMqttUri(),
                        g.getSample(),
                        g.getEnergyMode()
                );

                log.debug("Procesando Settings para Hub: {} (MsgId: {})", edgeId, messageId);
                manageSettingsUseCase.execute(domainSettings, messageId);
            }
        };
    }
}