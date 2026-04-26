package com.iot.managerservice.usecase.network;

import com.iot.managerservice.domain.model.NetworkSummary;
import com.iot.managerservice.domain.repository.HubRepository;
import com.iot.managerservice.domain.repository.NetworkRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de Uso de proyección de datos orientado a la visualización de la topología.
 * <p>
 * A diferencia de la consulta completa, este servicio construye un objeto de resumen
 * ({@link NetworkSummary}) ideal para la interfaz de usuario.
 * Orquesta consultas a múltiples repositorios para enriquecer los datos de la red
 * con métricas operativas, como la cantidad total de dispositivos Hubs conectados.
 * </p>
 */
@Service
public class GetNetworksByEdgeUseCase {

    private final NetworkRepository networkRepository;
    private final HubRepository hubRepository;

    public GetNetworksByEdgeUseCase(NetworkRepository networkRepository, HubRepository hubRepository) {
        this.networkRepository = networkRepository;
        this.hubRepository = hubRepository;
    }

    public List<NetworkSummary> execute(String edgeId) {
        return networkRepository.findByEdgeId(edgeId).stream()
                .map(network -> {
                    // Contamos los Hubs de esta red específica
                    long count = hubRepository.countByNetworkId(network.networkId());

                    return new NetworkSummary(
                            network.networkId(),
                            network.name(),
                            network.description(),
                            network.location(),
                            network.active(),
                            network.edgeId(),
                            count
                    );
                })
                .collect(Collectors.toList());
    }
}
