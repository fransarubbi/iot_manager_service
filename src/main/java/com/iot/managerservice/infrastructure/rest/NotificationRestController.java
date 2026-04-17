package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Notification;
import com.iot.managerservice.usecase.notification.ManageNotificationsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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
