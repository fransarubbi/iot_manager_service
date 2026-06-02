package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.application.grpc.generated.*;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import com.iot.managerservice.usecase.settings.ManageSettingsUseCase;
import com.iot.managerservice.usecase.settings.ProcessSettingOkUseCase;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcController {

    private final ManageSettingsUseCase manageSettingsUseCase;
    private final GrpcMessageSenderImpl messageSender;
    private final EdgeValidationCache edgeValidationCache;
    private final ManageNotificationsUseCase notificationUseCase;
    private final ProcessSettingOkUseCase processSettingOkUseCase;

    @GrpcClient("router-client")
    private ManagerServiceGrpc.ManagerServiceStub asyncStub;
    private StreamObserver<FromManager> requestObserver;

    @PostConstruct
    public void startConnection() {
        connectToRouter();
    }

    private void connectToRouter() {
        log.info("Iniciando conexión gRPC hacia el Router...");
        requestObserver = asyncStub.connectStream(new StreamObserver<ToManager>() {

            @Override
            public void onNext(ToManager message) {
                String edgeId = "";

                switch (message.getPayloadCase()) {
                    case HELLO_WORLD:
                        edgeId = message.getHelloWorld().getMetadata().getSenderUserId();
                        break;
                    case SETTINGS:
                        edgeId = message.getSettings().getMetadata().getSenderUserId();
                        break;
                    case SETTING_OK:
                        edgeId = message.getSettingOk().getMetadata().getSenderUserId();
                        break;
                    case FIRMWARE_OUTCOME:
                        edgeId = message.getFirmwareOutcome().getMetadata().getSenderUserId();
                        break;
                    default:
                        log.warn("Mensaje ignorado o sin payload: {}", message.getPayloadCase());
                        return;
                }

                // Barrera de Seguridad
                if (!edgeValidationCache.isValid(edgeId)) {
                    log.warn("ACCESO DENEGADO: Mensaje de un edge con id no registrado ({}).", edgeId);
                    return;
                }

                // CORRECCIÓN 3: Procesamiento limpio solo con los mensajes entrantes reales
                switch (message.getPayloadCase()) {
                    case HELLO_WORLD:
                        log.info("mensaje HelloWorld recibido de Edge: {}", edgeId);
                        String hwDesc = "edge_id: " + edgeId
                                + " timestamp: " + message.getHelloWorld().getMetadata().getTimestamp();
                        notificationUseCase.createNotification("HELLO_WORLD", hwDesc);
                        break;

                    case SETTINGS:
                        handleSettings(edgeId, message.getSettings());
                        break;

                    case SETTING_OK:
                        Long msgId = message.getSettingOk().getMessageId();
                        log.info("mensaje SettingOk recibido de Edge {}", edgeId);
                        processSettingOkUseCase.execute(edgeId, msgId, true, "Configuración aplicada en hardware");
                        break;

                    case FIRMWARE_OUTCOME:
                        log.info("mensaje FirmwareOutcome entrante");
                        String fwDesc = "edge_id: " + edgeId
                                + " timestamp: " + message.getFirmwareOutcome().getMetadata().getTimestamp()
                                + " network_id: " + message.getFirmwareOutcome().getNetwork()
                                + " is_ok: " + message.getFirmwareOutcome().getIsOk()
                                + " percentage: " + message.getFirmwareOutcome().getPercentageOk();
                        notificationUseCase.createNotification("FIRMWARE_OUTCOME", fwDesc);
                        break;

                    case PAYLOAD_NOT_SET:
                        log.warn("Se recibió un paquete vacío desde el Edge: {}", edgeId);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("El stream con el router se rompió: {}", t.getMessage());
                messageSender.setRouterStream(null);
                reconnect();
            }

            @Override
            public void onCompleted() {
                log.info("El Router cerró la conexión pacíficamente.");
                messageSender.setRouterStream(null);
                reconnect();
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
                log.debug("procesando Settings para Hub: {} (MsgId: {})", edgeId, messageId);
                manageSettingsUseCase.execute(domainSettings, messageId);
            }
        });

        messageSender.setRouterStream(requestObserver);
        log.info("Túnel gRPC con el Router establecido. Listo para enviar y recibir.");
    }

    public void sendMessageToRouter(FromManager message) {
        if (requestObserver != null) {
            requestObserver.onNext(message);
        } else {
            log.error("No se puede enviar el mensaje, el túnel está desconectado.");
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(5000); // Esperar 5 segundos antes de reintentar
            connectToRouter();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}