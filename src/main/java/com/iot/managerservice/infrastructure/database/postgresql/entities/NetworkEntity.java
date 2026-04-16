package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "networks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NetworkEntity {
    @Id
    @Column(name = "network_id")
    private String networkId;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;
}
