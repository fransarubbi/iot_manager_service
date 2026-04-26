package com.iot.managerservice.usecase.notification;

import com.iot.managerservice.domain.model.Notification;
import com.iot.managerservice.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

/**
 * Caso de Uso central para la orquestación del sistema de alertas y eventos del Manager.
 * <p>
 * Centraliza la lógica de negocio responsable de emitir nuevas advertencias, listar la bandeja
 * de eventos críticos y permitir la manipulación del estado de estas notificaciones por parte
 * de los operadores del sistema.
 * </p>
 */
@Service
public class ManageNotificationsUseCase {

    private final NotificationRepository repository;

    public ManageNotificationsUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public void createNotification(String type, String details) {
        Notification notification = new Notification(
                null,
                type,
                details,
                true,
                Instant.now().getEpochSecond()
        );
        repository.save(notification);
    }

    public List<Notification> getActiveNotifications() {
        return repository.findActive();
    }

    public void archiveNotification(Long id) {
        repository.markAsRead(id);
    }
}
