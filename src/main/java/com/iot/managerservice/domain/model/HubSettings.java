package com.iot.managerservice.domain.model;


public record HubSettings(
        String hubId,
        String networkId,
        String deviceName,
        String wifiSsid,
        String wifiPassword,
        String mqttUri,
        Integer sample,
        Integer energyMode
) {}