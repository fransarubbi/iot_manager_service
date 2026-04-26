package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa la tabla {@code notifications} en la base de datos.
 * <p>
 * Almacena el historial de alertas y eventos generados por el sistema.
 * Es la única entidad del sistema que delega la generación de su identificador principal
 * a la base de datos relacional.
 * </p>
 */
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