package com.iot.managerservice.domain.port;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.model.Network;


public interface GrpcMessageSender {
    void sendAck(String edgeId, HubSettings hub, Long messageId);
    void sendNetworkUpdate(Network network, String operation, long unixTimestamp);
    void sendHubSettings(String edgeId, Long messageId, HubSettings settings);
}
