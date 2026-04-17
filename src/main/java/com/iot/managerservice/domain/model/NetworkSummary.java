package com.iot.managerservice.domain.model;

public record NetworkSummary(
        String networkId,
        String name,
        String description,
        String location,
        boolean active,
        String edgeId,
        long hubCount
) {}