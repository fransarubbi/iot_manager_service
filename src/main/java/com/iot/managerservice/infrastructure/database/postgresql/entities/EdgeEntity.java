package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa la tabla {@code edges} en la base de datos relacional.
 * <p>
 * Almacena toda la parametrización de red, topología y políticas de tolerancia a fallos
 * que rigen el comportamiento de un dispositivo Edge. La estructura de esta tabla
 * es intencionalmente plana y desnormalizada (sin claves foráneas complejas a nivel JPA)
 * para favorecer la rápida lectura y exportación hacia archivos TOML.
 * </p>
 */
@Entity
@Table(name = "edges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EdgeEntity {
    @Id
    @Column(name = "edge_id")
    private String edgeId;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "cn")
    private String cn;

    @Column(name = "host_server")
    private String hostServer;

    @Column(name = "host_port")
    private Integer hostPort;

    @Column(name = "host_local")
    private String hostLocal;

    @Column(name = "data_base_path")
    private String dataBasePath;

    @Column(name = "buffer_length")
    private Integer bufferLength;

    @Column(name = "log_level")
    private String logLevel;

    @Column(name = "max_number_handshake_attempts")
    private Integer maxNumberHandshakeAttempts;

    @Column(name = "frequency_messages_phase")
    private Integer frequencyMessagesPhase;

    @Column(name = "frequency_messages_safemode")
    private Integer frequencyMessagesSafeMode;

    @Column(name = "handshake_time_limit")
    private Integer handshakeTimeLimit;

    @Column(name = "phase_time_limit")
    private Integer phaseTimeLimit;

    @Column(name = "safemode_time_limit")
    private Integer safeModeTimeLimit;

    @Column(name = "heartbeat_balancemode_time")
    private Integer heartbeatBalanceModeTime;

    @Column(name = "heartbeat_normal_time")
    private Integer heartbeatNormalTime;

    @Column(name = "heartbeat_safemode_time")
    private Integer heartbeatSafeModeTime;
}