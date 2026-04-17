package com.iot.managerservice.usecase.hub;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.repository.HubRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class GetHubsByNetworkUseCase {

    private final HubRepository hubRepository;

    public GetHubsByNetworkUseCase(HubRepository hubRepository) {
        this.hubRepository = hubRepository;
    }

    public List<HubSettings> execute(String networkId) {
        return hubRepository.findByNetworkId(networkId);
    }
}
