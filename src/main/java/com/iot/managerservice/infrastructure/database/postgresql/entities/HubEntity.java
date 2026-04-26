package com.iot.managerservice.infrastructure.database.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que mapea la tabla {@code hubs} en PostgreSQL.
 * <p>
 * Almacena tanto la configuración operativa (redes Wi-Fi, MQTT, tiempos de muestreo)
 * que el Hub necesita para funcionar, como un registro crucial del estado de sincronización
 * ({@code message_id}).
 * </p>
 */
@Entity
@Table(name = "hubs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HubEntity {
    @Id
    @Column(name = "hub_id")
    private String hubId;

    @Column(name = "network_id")
    private String networkId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "wifi_ssid")
    private String wifiSsid;

    @Column(name = "wifi_password")
    private String wifiPassword;

    @Column(name = "mqtt_uri")
    private String mqttUri;

    @Column(name = "sample")
    private Integer sample;

    @Column(name = "energy_mode")
    private Integer energyMode;

    @Column(name = "message_id")
    private Long messageId;
}