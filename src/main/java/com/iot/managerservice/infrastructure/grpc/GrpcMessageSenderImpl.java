package com.iot.managerservice.infrastructure.grpc;

import com.iot.managerservice.domain.port.GrpcMessageSender;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class GrpcMessageSenderImpl implements GrpcMessageSender {

    @Override
    public void sendAck(String hubId) {
        // Por ahora un log. Más adelante conectar esto al StreamObserver.
        log.info(">> ENVIANDO ACK VÍA gRPC AL HUB: {} <<", hubId);
    }
}