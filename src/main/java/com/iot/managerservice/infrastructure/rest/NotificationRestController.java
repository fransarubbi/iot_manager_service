package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Notification;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST (Adaptador Primario/Entrada) para el sistema de alertas y eventos.
 * <p>
 * Actúa como la bandeja de entrada para el dashboard administrativo, permitiendo
 * leer las notificaciones pendientes y descartarlas una vez gestionadas.
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final ManageNotificationsUseCase useCase;

    public NotificationRestController(ManageNotificationsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getActive() {
        return ResponseEntity.ok(useCase.getActiveNotifications());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        useCase.archiveNotification(id);
        return ResponseEntity.noContent().build();
    }
}
