package com.iot.managerservice.infrastructure.database.postgresql.entities;

import com.iot.managerservice.domain.model.DeviceType;
import jakarta.persistence.*;
import lombok.*;


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
