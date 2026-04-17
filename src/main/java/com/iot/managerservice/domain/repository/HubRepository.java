package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings;
import java.util.List;
import java.util.Optional;


public interface HubRepository {
    void save(HubSettings settings, Long messageId);
    void update(HubSettings settings, Long messageId);
    List<HubIdAndVersion> getAllHubVersions();
    List<HubSettings> findByNetworkId(String networkId);
    long countByNetworkId(String networkId);
    Optional<HubSettings> findById(String hubId);
}