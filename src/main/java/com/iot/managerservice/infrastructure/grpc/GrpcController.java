package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.application.grpc.generated.*;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import com.iot.managerservice.usecase.settings.ManageSettingsUseCase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@GrpcService
public class GrpcController extends ManagerServiceGrpc.ManagerServiceImplBase {

    private final ManageSettingsUseCase manageSettingsUseCase;
    private final GrpcMessageSenderImpl messageSender;
    private final EdgeValidationCache edgeValidationCache;
    private final ManageNotificationsUseCase notificationUseCase;

    // Inyectamos ambas clases
    public GrpcController(ManageSettingsUseCase manageSettingsUseCase,
                          GrpcMessageSenderImpl messageSender,
                          EdgeValidationCache edgeValidationCache,
                          ManageNotificationsUseCase notificationUseCase) {
        this.manageSettingsUseCase = manageSettingsUseCase;
        this.messageSender = messageSender;
        this.edgeValidationCache = edgeValidationCache;
        this.notificationUseCase = notificationUseCase;
    }

    @Override
    public StreamObserver<ToManager> connectStream(StreamObserver<FromManager> responseObserver) {

        log.info("¡El Router ha iniciado la conexión bidireccional!");

        // Se guarda la tubería de salida en el Sender para usarla despues
        messageSender.setRouterStream(responseObserver);

        // Retornamos el Observer que se queda ESCUCHANDO infinitamente
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
                        handleSettingsOk(edgeId, request.getSettingOk());
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

            private void handleSettingsOk(String edgeId, SettingOk g) {
                // ACA SE DEBE GUARDAR LA NUEVA CONFIGURACION
            }
        };
    }
}