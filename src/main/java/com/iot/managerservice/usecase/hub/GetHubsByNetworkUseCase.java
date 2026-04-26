package com.iot.managerservice.usecase.hub;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.domain.repository.HubRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Caso de Uso para la inspección de la topología local de una red.
 * <p>
 * Actúa como intermediario analítico aislando la capa de presentación de la consulta de datos.
 * Recupera de forma exclusiva las configuraciones operativas de aquellos Hubs que se encuentran
 * enlazados a un segmento lógico de red particular.
 * </p>
 */
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
