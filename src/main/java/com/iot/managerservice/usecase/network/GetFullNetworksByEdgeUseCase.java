package com.iot.managerservice.usecase.network;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.repository.NetworkRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Caso de Uso destinado a la consulta detallada de la topología de red.
 * <p>
 * Provee un mecanismo de solo lectura para obtener las entidades puras y completas
 * de las Redes lógicas que se encuentran bajo la administración directa de un
 * dispositivo Edge específico.
 * </p>
 */
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
