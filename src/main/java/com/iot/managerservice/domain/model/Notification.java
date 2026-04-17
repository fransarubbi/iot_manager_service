package com.iot.managerservice.domain.model;

public record Notification(
        Long id,
        String type,
        String description,
        boolean active,
        long createdAt
) {}