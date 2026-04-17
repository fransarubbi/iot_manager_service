package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.application.grpc.generated.FromManager;
import com.iot.managerservice.application.grpc.generated.Metadata;
import com.iot.managerservice.application.grpc.generated.SettingOk;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.port.GrpcMessageSender;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;


@Slf4j
@Component
public class GrpcMessageSenderImpl implements GrpcMessageSender {

    private StreamObserver<FromManager> routerOutboundStream;

    // Método para inyectar la tubería cuando el Router se conecta
    public void setRouterStream(StreamObserver<FromManager> stream) {
        this.routerOutboundStream = stream;
        log.info("Tubería de salida hacia el Router enlazada exitosamente.");
    }

    @Override
    public void sendAck(String edgeId, HubSettings hub, Long messageId) {
        if (routerOutboundStream == null) {
            log.error("No se puede enviar ACK para el Hub {}. ¡El Router no está conectado!", hub.hubId());
            return;
        }

        try {
            Metadata metadata = Metadata.newBuilder()
                    .setSenderUserId("server0")
                    .setDestinationId(hub.hubId())
                    .setTimestamp(Instant.now().getEpochSecond())
                    .build();

            SettingOk ackPayload = SettingOk.newBuilder()
                    .setMetadata(metadata)
                    .setMessageId(messageId)
                    .setNetwork(hub.networkId())
                    .setHandshake(true)
                    .build();

            FromManager outgoingMessage = FromManager.newBuilder()
                    .setEdgeId(edgeId)
                    .setSettingOk(ackPayload)
                    .build();

            routerOutboundStream.onNext(outgoingMessage);
            log.info("ACK enviado al Router dirigido al Hub: {}", hub.hubId());

        } catch (Exception e) {
            log.error("Error al enviar el mensaje por gRPC: {}", e.getMessage());
        }
    }

    @Override
    public void sendNetworkUpdate(Network network, String operation, long unixTimestamp) {
        if (routerOutboundStream == null) {
            log.warn("No se notificó la actualización de Red {}. ¡El Router no está conectado!", network.networkId());
            return;
        }

        try {
            Metadata metadata = Metadata.newBuilder()
                    .setSenderUserId("server0")
                    .setDestinationId(network.edgeId())
                    .setTimestamp(unixTimestamp)
                    .build();

            com.iot.managerservice.application.grpc.generated.Network.Builder networkBuilder =
                    com.iot.managerservice.application.grpc.generated.Network.newBuilder()
                            .setMetadata(metadata)
                            .setIdNetwork(network.networkId())
                            .setNameNetwork(network.name());

            switch (operation) {
                case "CREATE":
                    networkBuilder.setActive(true);
                    networkBuilder.setDeleteNetwork(false);
                    break;

                case "UPDATE":
                    networkBuilder.setActive(network.active());
                    networkBuilder.setDeleteNetwork(false);
                    break;

                case "DELETE":
                    networkBuilder.setActive(false);
                    networkBuilder.setDeleteNetwork(true);
                    break;

                default:
                    log.warn("Operación de red desconocida: {}", operation);
            }

            com.iot.managerservice.application.grpc.generated.Network networkPayload = networkBuilder.build();

            FromManager outgoingMessage = FromManager.newBuilder()
                    .setEdgeId(network.edgeId())
                    .setNetwork(networkPayload)
                    .build();

            routerOutboundStream.onNext(outgoingMessage);
            log.info("Mensaje gRPC enviado al Edge {} para la red {}", network.edgeId(), network.networkId());

        } catch (Exception e) {
            log.error("Fallo al enviar notificación gRPC de Red: {}", e.getMessage());
        }
    }

    @Override
    public void sendHubSettings(String edgeId, Long messageId, HubSettings settings) {
        if (routerOutboundStream == null) return;

        Metadata metadata = Metadata.newBuilder()
                .setSenderUserId("server0")
                .setDestinationId(settings.hubId())
                .setTimestamp(Instant.now().getEpochSecond())
                .build();

        com.iot.managerservice.application.grpc.generated.Settings settingsPayload =
                com.iot.managerservice.application.grpc.generated.Settings.newBuilder()
                        .setMetadata(metadata)
                        .setMessageId(messageId)
                        .setNetwork(settings.networkId())
                        .setDeviceName(settings.deviceName())
                        .setWifiSsid(settings.wifiSsid())
                        .setWifiPassword(settings.wifiPassword())
                        .setMqttUri(settings.mqttUri())
                        .setSample(settings.sample())
                        .setEnergyMode(settings.energyMode())
                        .build();

        FromManager outgoingMessage = FromManager.newBuilder()
                .setEdgeId(edgeId)
                .setSettings(settingsPayload)
                .build();

        routerOutboundStream.onNext(outgoingMessage);
    }
}