package com.iot.managerservice.usecase.edge;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.domain.repository.EdgeRepository;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RegisterEdgeUseCase {

    private final EdgeRepository repository;
    private final EdgeValidationCache ramCache;
    private final EdgeConfigExporter configExporter;

    public RegisterEdgeUseCase(EdgeRepository repository,
                               EdgeValidationCache ramCache,
                               EdgeConfigExporter configExporter) {
        this.repository = repository;
        this.ramCache = ramCache;
        this.configExporter = configExporter;
    }

    @Transactional
    public void execute(Edge edge) {
        // Guardar en base de datos
        repository.save(edge);

        // Autorizar en RAM (para validacion en gRPC)
        ramCache.addEdgeToRam(edge.edgeId());

        // Generar archivos TOML en el servidor
        configExporter.generateConfiguration(edge);
    }
}