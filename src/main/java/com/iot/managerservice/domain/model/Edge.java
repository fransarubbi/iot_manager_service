package com.iot.managerservice.domain.model;


public record Edge(
        String edgeId,
        String name,
        String location,
        String cn,
        String hostServer,
        Integer hostPort,
        String hostLocal,
        String dataBasePath,
        Integer bufferLength,
        String logLevel,
        Integer maxNumberHandshakeAttempts,
        Integer frequencyMessagesPhase,
        Integer frequencyMessagesSafeMode,
        Integer handshakeTimeLimit,
        Integer phaseTimeLimit,
        Integer safeModeTimeLimit,
        Integer heartbeatBalanceModeTime,
        Integer heartbeatNormalTime,
        Integer heartbeatSafeModeTime
) {}