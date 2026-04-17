package com.iot.managerservice.domain.model;


public record Network(
        String networkId,
        String name,
        String description,
        String location,
        boolean active,
        String edgeId
) {}