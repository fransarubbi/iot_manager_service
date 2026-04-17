package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.HubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface JpaHubRepository extends JpaRepository<HubEntity, String> {
    // Busca todos los Hubs de una red especifica
    List<HubEntity> findByNetworkId(String networkId);

    // Cuenta cuántos Hubs hay en una red
    long countByNetworkId(String networkId);
}