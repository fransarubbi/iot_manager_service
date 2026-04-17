package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByActiveTrue();
}
