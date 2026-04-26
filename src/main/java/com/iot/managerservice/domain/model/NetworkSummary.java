package com.iot.managerservice.domain.model;

/**
 * Modelo de lectura o resumen (DTO de dominio) de una red IoT.
 * <p>
 * Proyecta la información base de una entidad {@link Network} agregando métricas
 * consolidadas, como el número total de dispositivos Hub asociados. Está diseñado
 * para facilitar la visualización en el frontend sin necesidad de ejecutar múltiples
 * consultas al repositorio.
 * </p>
 *
 * @param networkId   Identificador lógico y único de la red.
 * @param name        Nombre descriptivo de la red.
 * @param description Breve explicación del propósito de esta red.
 * @param location    Ubicación física de la red.
 * @param active      Estado operativo actual de la red.
 * @param edgeId      Identificador del Edge responsable de la red.
 * @param hubCount    Cantidad total de Hubs que están registrados y pertenecen a esta red.
 */
public record NetworkSummary(
        String networkId,
        String name,
        String description,
        String location,
        boolean active,
        String edgeId,
        long hubCount
) {}