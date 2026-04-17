package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.Notification;
import java.util.List;


public interface NotificationRepository {
    void save(Notification notification);
    List<Notification> findActive();
    void markAsRead(Long id);
}
