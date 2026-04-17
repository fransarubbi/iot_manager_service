package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import com.iot.managerservice.domain.model.HubSettings; // NUEVO
import java.util.List;


public interface HubRepository {
    void save(HubSettings settings, Long messageId);
    void update(HubSettings settings, Long messageId);

    List<HubIdAndVersion> getAllHubVersions();
}