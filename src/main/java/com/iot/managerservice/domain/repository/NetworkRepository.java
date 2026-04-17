package com.iot.managerservice.domain.repository;


import com.iot.managerservice.domain.model.Network;
import java.util.List;
import java.util.Optional;


public interface NetworkRepository {
    void save(Network network);
    Optional<Network> findById(String networkId);
    List<Network> findAll();
    void deleteById(String networkId);
    void updateActiveStatus(String networkId, boolean active);
}