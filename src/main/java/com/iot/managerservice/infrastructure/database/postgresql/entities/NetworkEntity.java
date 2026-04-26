package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que mapea la tabla {@code networks} en PostgreSQL.
 * <p>
 * Representa la estructura de persistencia de las agrupaciones lógicas de dispositivos (Hubs).
 * Permite relacionar múltiples Hubs bajo el paraguas de una misma red y administrarlos
 * a través de un único Edge controlador.
 * </p>
 */
@Entity
@Table(name = "networks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NetworkEntity {
    @Id
    @Column(name = "network_id")
    private String networkId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "active")
    private boolean active;

    @Column(name = "edge_id")
    private String edgeId;
}
