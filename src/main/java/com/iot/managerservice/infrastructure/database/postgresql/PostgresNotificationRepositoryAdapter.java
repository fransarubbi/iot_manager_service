package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.Notification;
import com.iot.managerservice.domain.repository.NotificationRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.NotificationEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaNotificationRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class PostgresNotificationRepositoryAdapter implements NotificationRepository {

    private final JpaNotificationRepository jpaRepository;

    public PostgresNotificationRepositoryAdapter(JpaNotificationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Notification n) {
        NotificationEntity entity = new NotificationEntity();
        entity.setType(n.type());
        entity.setDescription(n.description());
        entity.setActive(n.active());
        entity.setCreatedAt(n.createdAt());
        jpaRepository.save(entity);
    }

    @Override
    public List<Notification> findActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(e -> new Notification(e.getId(), e.getType(), e.getDescription(), e.isActive(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long id) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.setActive(false);
            jpaRepository.save(e);
        });
    }
}
