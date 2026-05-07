package com.iot.managerservice.infrastructure.database.postgresql.entities;

import com.iot.managerservice.domain.model.DeviceType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


/**
 * Entidad JPA que mapea la persistencia de los certificados criptográficos en PostgreSQL.
 * <p>
 * Representa la tabla {@code certificates}. Esta clase se utiliza exclusivamente en la capa
 * de infraestructura para convertir los registros de la base de datos en objetos de Java,
 * y posteriormente ser transformados a la entidad de dominio {@link com.iot.managerservice.domain.model.CertificateData}
 * mediante un mapeador (Mapper).
 * </p>
 */
@Entity
@Table(name = "certificates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CertificateEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "emission_date", nullable = false)
    private Instant emissionDate;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "private_key_pem", columnDefinition = "TEXT", nullable = false)
    private String privateKeyPem;

    @Column(name = "certificate_pem", columnDefinition = "TEXT", nullable = false)
    private String certificatePem;
}
