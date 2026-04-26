package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio Spring Data JPA responsable de gestionar el historial de alertas mediante la entidad {@link NotificationEntity}.
 * <p>
 * Interactúa con la tabla {@code notifications}. A diferencia de los otros repositorios
 * de este sistema que utilizan {@code String} (UUIDs) como clave primaria, este utiliza
 * un tipo {@code Long} autoincremental nativo de la base de datos.
 * </p>
 */
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * Consulta y retorna todas las notificaciones que se encuentran activas en el sistema,
     * es decir, aquellas que requieren atención o no han sido marcadas como "leídas" por un administrador.
     * <p>
     * Su resolución SQL es equivalente a {@code SELECT * FROM notifications WHERE active = true}.
     * </p>
     *
     * @return Lista de notificaciones pendientes.
     */
    List<NotificationEntity> findByActiveTrue();
}
