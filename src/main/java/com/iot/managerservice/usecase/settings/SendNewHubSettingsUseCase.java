package com.iot.managerservice.usecase.settings;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.port.GrpcMessageSender;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.domain.repository.NetworkRepository;
import com.iot.managerservice.infrastructure.cache.PendingSettingsCache;
import org.springframework.stereotype.Service;


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