package com.iot.managerservice.domain.model;

/**
 * Representa la configuración operativa y de conectividad destinada a un dispositivo Hub.
 * <p>
 * Esta entidad encapsula todos los parámetros que un Hub físico necesita para conectarse
 * a la red local, comunicarse de forma segura con el broker MQTT y definir su
 * comportamiento de captura de telemetría y eficiencia energética.
 * </p>
 *
 * @param hubId        Identificador único del Hub al cual pertenece esta configuración.
 * @param networkId    Identificador de la red lógica a la que el Hub ha sido asignado.
 * @param deviceName   Nombre legible asignado al dispositivo Hub para facilitar su identificación.
 * @param wifiSsid     Nombre de la red WiFi (SSID) a la que el microcontrolador debe conectarse.
 * @param wifiPassword Contraseña en texto plano para el acceso a la red WiFi.
 * @param mqttUri      Dirección o URI del broker MQTT al cual se enviará la telemetría.
 * @param sample       Frecuencia o intervalo de muestreo en el que los sensores del Hub leen y envían datos.
 * @param energyMode   Nivel o perfil de consumo energético del Hub.
 */
public record HubSettings(
        String hubId,
        String networkId,
        String deviceName,
        String wifiSsid,
        String wifiPassword,
        String mqttUri,
        Integer sample,
        Integer energyMode
) {}