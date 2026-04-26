package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.Notification;
import java.util.List;

/**
 * Puerto de persistencia para la gestión de alertas y notificaciones del sistema.
 * <p>
 * Permite registrar eventos asíncronos y consultar aquellas alertas que
 * requieren atención por parte del administrador.
 * </p>
 */
public interface NotificationRepository {

    /**
     * Persiste una nueva notificación o evento en el registro del sistema.
     *
     * @param notification Entidad que contiene los detalles de la alerta.
     */
    void save(Notification notification);

    /**
     * Recupera todas las notificaciones que aún no han sido revisadas o descartadas.
     *
     * @return Lista de notificaciones en estado activo.
     */
    List<Notification> findActive();

    /**
     * Marca una notificación específica como leída o inactiva, sacándola de la bandeja principal.
     *
     * @param id Identificador único de la notificación a actualizar.
     */
    void markAsRead(Long id);
}