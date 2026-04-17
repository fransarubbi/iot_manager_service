package com.iot.managerservice.infrastructure.database.postgresql.repositories;

import com.iot.managerservice.infrastructure.database.postgresql.entities.NetworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface JpaNetworkRepository extends JpaRepository<NetworkEntity, String> {
    // Busca todas las redes de un Edge especifico
    List<NetworkEntity> findByEdgeId(String edgeId);
}