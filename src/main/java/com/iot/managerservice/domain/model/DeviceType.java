package com.iot.managerservice.domain.model;

/**
 * Define los diferentes tipos de dispositivos que pueden operar dentro de la red IoT.
 * <p>
 * Esta enumeración se utiliza en el dominio para categorizar, enrutar
 * y aplicar lógicas específicas de configuración o reglas de emisión
 * de certificados según el rol jerárquico del nodo.
 * </p>
 */
public enum DeviceType {
    /**
     * Actúa como intermediario y coordinador, agrupando
     * múltiples dispositivos Hub y enrutando sus datos hacia el Servidor.
     */
    EDGE,

    /**
     *  Microcontrolador que se encarga de la recolección directa de datos
     *  de sensores.
     */
    HUB,

    /**
     * Enrutador (Router). Dispositivo encargado de gestionar el tráfico de red
     * a mayor escala, comunicando subredes IoT con los servicios backend centrales.
     */
    ROUTER
}