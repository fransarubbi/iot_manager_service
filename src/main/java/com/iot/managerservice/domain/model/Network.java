package com.iot.managerservice.domain.model;

/**
 * Entidad de dominio que define una red lógica de dispositivos IoT.
 * <p>
 * Una red actúa como una agrupación jerárquica que asocia múltiples Hubs
 * bajo el control y la supervisión de un único dispositivo de borde (Edge).
 * </p>
 *
 * @param networkId   Identificador lógico y único de la red.
 * @param name        Nombre descriptivo de la red (ej. "Red Sensores Pabellón A").
 * @param description Breve explicación del propósito o las características operativas de esta red.
 * @param location    Ubicación física o área lógica que abarca la red.
 * @param active      Indica si la red se encuentra actualmente operativa y habilitada en el sistema.
 * @param edgeId      Identificador del dispositivo Edge que administra y consolida los datos de los Hubs en esta red.
 */
public record Network(
        String networkId,
        String name,
        String description,
        String location,
        boolean active,
        String edgeId
) {}