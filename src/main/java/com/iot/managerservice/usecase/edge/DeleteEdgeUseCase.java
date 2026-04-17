package com.iot.managerservice.usecase.edge;

import com.iot.managerservice.domain.repository.EdgeRepository;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class DeleteEdgeUseCase {

    private final EdgeRepository repository;
    private final EdgeValidationCache ramCache;
    private final EdgeConfigExporter configExporter;

    public DeleteEdgeUseCase(EdgeRepository repository,
                             EdgeValidationCache ramCache,
                             EdgeConfigExporter configExporter) {
        this.repository = repository;
        this.ramCache = ramCache;
        this.configExporter = configExporter;
    }

    @Transactional
    public void execute(String edgeId) {
        log.info("Iniciando proceso de BAJA para el Edge: {}", edgeId);

        // Eliminar de la Base de Datos
        repository.deleteById(edgeId);

        // Eliminar de la RAM
        ramCache.removeEdgeFromRam(edgeId);

        // Eliminar los archivos físicos (.toml y su carpeta)
        configExporter.deleteConfiguration(edgeId);

        log.info("Proceso de BAJA completado exitosamente para el Edge: {}", edgeId);
    }
}