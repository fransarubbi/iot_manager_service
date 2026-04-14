package com.iot.managerservice.domain.repository;

import com.iot.managerservice.domain.model.HubIdAndVersion;
import java.util.List;


public interface HubRepository {
    void save(String hubId, Long messageId, Object payload);
    void update(String hubId, Long messageId, Object payload);
    List<HubIdAndVersion> getAllHubVersions();
}