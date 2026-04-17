package com.iot.managerservice.usecase.network;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.repository.NetworkRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class GetFullNetworksByEdgeUseCase {

    private final NetworkRepository repository;

    public GetFullNetworksByEdgeUseCase(NetworkRepository repository) {
        this.repository = repository;
    }

    public List<Network> execute(String edgeId) {
        return repository.findByEdgeId(edgeId);
    }
}
