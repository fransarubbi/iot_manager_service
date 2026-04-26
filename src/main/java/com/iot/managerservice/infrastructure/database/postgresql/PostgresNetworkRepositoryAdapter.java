package com.iot.managerservice.infrastructure.database.postgresql;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.repository.NetworkRepository;
import com.iot.managerservice.infrastructure.database.postgresql.entities.NetworkEntity;
import com.iot.managerservice.infrastructure.database.postgresql.repositories.JpaNetworkRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de base de datos relacional para gestionar las agrupaciones lógicas de red (Networks).
 * <p>
 * Conecta los requerimientos del dominio (puerto {@link NetworkRepository}) con
 * las capacidades de consulta de Spring Data JPA.
 * </p>
 */
@Repository
public class PostgresNetworkRepositoryAdapter implements NetworkRepository {

    private final JpaNetworkRepository jpaRepository;

    public PostgresNetworkRepositoryAdapter(JpaNetworkRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Network network) {
        NetworkEntity entity = new NetworkEntity();
        entity.setNetworkId(network.networkId());
        entity.setName(network.name());
        entity.setDescription(network.description());
        entity.setLocation(network.location());
        entity.setActive(network.active());
        entity.setEdgeId(network.edgeId());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Network> findById(String networkId) {
        return jpaRepository.findById(networkId)
                .map(e -> new Network(e.getNetworkId(), e.getName(),
                        e.getDescription(), e.getLocation(), e.isActive(), e.getEdgeId()));
    }

    @Override
    public List<Network> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> new Network(e.getNetworkId(), e.getName(),
                        e.getDescription(), e.getLocation(), e.isActive(), e.getEdgeId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String networkId) {
        jpaRepository.deleteById(networkId);
    }

    @Override
    public void updateActiveStatus(String networkId, boolean active) {
        jpaRepository.findById(networkId).ifPresent(entity -> {
            entity.setActive(active);
            jpaRepository.save(entity);
        });
    }

    @Override
    public List<Network> findByEdgeId(String edgeId) {
        return jpaRepository.findByEdgeId(edgeId).stream()
                .map(e -> new Network(e.getNetworkId(), e.getName(), e.getDescription(), e.getLocation(), e.isActive(), e.getEdgeId()))
                .collect(Collectors.toList());
    }
}