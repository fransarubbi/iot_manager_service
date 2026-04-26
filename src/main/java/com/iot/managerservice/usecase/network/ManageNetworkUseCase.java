package com.iot.managerservice.usecase.network;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.repository.NetworkRepository;
import com.iot.managerservice.domain.port.GrpcMessageSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;


/**
 * Caso de Uso orquestador para las mutaciones en la topología lógica de la infraestructura IoT.
 * <p>
 * Centraliza la lógica para la creación, actualización y eliminación de redes. Su responsabilidad
 * principal es garantizar la consistencia entre el estado del servidor central (PostgreSQL) y
 * el estado físico de la red, enviando notificaciones de actualización en tiempo real hacia el
 * hardware (mediante gRPC) cada vez que ocurre un cambio.
 * </p>
 */
@Slf4j
@Service
public class ManageNetworkUseCase {

    private final NetworkRepository networkRepository;
    private final GrpcMessageSender grpcMessageSender;

    public ManageNetworkUseCase(NetworkRepository networkRepository, GrpcMessageSender grpcMessageSender) {
        this.networkRepository = networkRepository;
        this.grpcMessageSender = grpcMessageSender;
    }

    @Transactional
    public void createNetwork(Network network) {
        networkRepository.save(network);
        long unixTimestamp = Instant.now().getEpochSecond();
        grpcMessageSender.sendNetworkUpdate(network, "CREATE", unixTimestamp);
    }

    @Transactional
    public void updateNetwork(String networkId) {
        networkRepository.updateActiveStatus(networkId, false);

        networkRepository.findById(networkId).ifPresent(network -> {
            long unixTimestamp = Instant.now().getEpochSecond();
            grpcMessageSender.sendNetworkUpdate(network, "UPDATE", unixTimestamp);
            log.info("Red actualizada y notificada: {}", networkId);
        });
    }

    @Transactional
    public void deleteNetwork(String networkId) {
        networkRepository.findById(networkId).ifPresent(network -> {
            networkRepository.deleteById(networkId);

            long unixTimestamp = Instant.now().getEpochSecond();
            grpcMessageSender.sendNetworkUpdate(network, "DELETE", unixTimestamp);
            log.info("Red eliminada y notificada: {}", networkId);
        });
    }
}