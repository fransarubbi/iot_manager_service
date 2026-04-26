package com.iot.managerservice.usecase.edge;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.repository.EdgeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Caso de Uso orientado a la consulta del inventario de infraestructura.
 * <p>
 * Provee un mecanismo de solo lectura para recuperar el catálogo completo de todos los
 * dispositivos Edge actualmente registrados y gestionados por el Manager.
 * </p>
 */
@Service
public class GetAllEdgesUseCase {

    private final EdgeRepository repository;

    public GetAllEdgesUseCase(EdgeRepository repository) {
        this.repository = repository;
    }

    public List<Edge> execute() {
        return repository.findAll();
    }
}
