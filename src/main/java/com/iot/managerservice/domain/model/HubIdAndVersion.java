package com.iot.managerservice.domain.model;

/**
 * Objeto de valor utilizado para asociar un Hub con la versión de su último mensaje procesado.
 * <p>
 * Se emplea en el mecanismo de caché o validación para asegurar la idempotencia
 * de las operaciones, sincronizar el estado del firmware/configuración, o evitar el
 * procesamiento de telemetría duplicada u obsoleta.
 * </p>
 *
 * @param hubId         Identificador único del dispositivo Hub.
 * @param lastMessageId Identificador numérico del último mensaje validado.
 */
public record HubIdAndVersion(
        String hubId,
        Long lastMessageId
) {}