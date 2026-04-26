package com.iot.managerservice.domain.model;

/**
 * Representa una notificación.
 * <p>
 * Permite registrar y alertar a los administradores del sistema sobre eventos clave,
 * como fallos de conectividad en los Edges, expiración de certificados mTLS,
 * o advertencias operativas en los Hubs.
 * </p>
 *
 * @param id          Identificador único autoincremental de la notificación.
 * @param type        Categoría o nivel de severidad del evento (ej. "INFO", "WARNING", "ERROR").
 * @param description Mensaje de texto detallado que describe el suceso ocurrido.
 * @param active      Bandera que indica si la notificación está pendiente de revisión (activa) o si ya fue descartada/marcada como leída.
 * @param createdAt   Marca de tiempo (Unix timestamp en milisegundos) del momento exacto en que se generó la alerta.
 */
public record Notification(
        Long id,
        String type,
        String description,
        boolean active,
        long createdAt
) {}