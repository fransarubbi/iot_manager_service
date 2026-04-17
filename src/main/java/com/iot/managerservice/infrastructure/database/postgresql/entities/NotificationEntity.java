package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NotificationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private long createdAt;
}