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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "cn", nullable = false)
    private String cn;

    @Column(name = "host_server", nullable = false)
    private String hostServer;

    @Column(name = "host_port", nullable = false)
    private Integer hostPort;

    @Column(name = "host_local", nullable = false)
    private String hostLocal;

    @Column(name = "data_base_path", nullable = false)
    private String dataBasePath;

    @Column(name = "buffer_length", nullable = false)
    private Integer bufferLength;

    @Column(name = "log_level", nullable = false)
    private String logLevel;

    @Column(name = "max_number_handshake_attempts", nullable = false)
    private Integer maxNumberHandshakeAttempts;

    @Column(name = "frequency_messages_phase", nullable = false)
    private Integer frequencyMessagesPhase;

    @Column(name = "frequency_messages_safemode", nullable = false)
    private Integer frequencyMessagesSafeMode;

    @Column(name = "handshake_time_limit", nullable = false)
    private Integer handshakeTimeLimit;

    @Column(name = "phase_time_limit", nullable = false)
    private Integer phaseTimeLimit;

    @Column(name = "safemode_time_limit", nullable = false)
    private Integer safeModeTimeLimit;

    @Column(name = "heartbeat_balancemode_time", nullable = false)
    private Integer heartbeatBalanceModeTime;

    @Column(name = "heartbeat_normal_time", nullable = false)
    private Integer heartbeatNormalTime;

    @Column(name = "heartbeat_safemode_time", nullable = false)
    private Integer heartbeatSafeModeTime;
}