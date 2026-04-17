package com.iot.managerservice.infrastructure.database.postgresql.entities;

import com.iot.managerservice.domain.model.DeviceType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "certificates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CertificateEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "display_name")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "status")
    private String status;

    @Column(name = "emission_date")
    private long emissionDate;

    @Column(name = "expiration_date")
    private long expirationDate;

    // Usamos TEXT porque los PEM son strings largos
    @Column(name = "private_key_pem", columnDefinition = "TEXT")
    private String privateKeyPem;

    @Column(name = "certificate_pem", columnDefinition = "TEXT")
    private String certificatePem;
}
