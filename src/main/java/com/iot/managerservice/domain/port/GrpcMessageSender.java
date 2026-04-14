package com.iot.managerservice.domain.port;

public interface GrpcMessageSender {
    void sendAck(String hubId);
}
