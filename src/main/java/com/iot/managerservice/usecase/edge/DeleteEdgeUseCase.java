package com.iot.managerservice.usecase.edge;

import com.iot.managerservice.domain.repository.EdgeRepository;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import com.iot.managerservice.infrastructure.cache.EdgeValidationCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de Uso para gestionar la baja o desmantelamiento de un dispositivo Edge.
 * <p>
 * Coordina la eliminación segura de todos los rastros del dispositivo en la infraestructura del Manager.
 * Para mantener la consistencia del sistema, este proceso retira el registro de la persistencia,
 * revoca el acceso en la caché gRPC y purga los artefactos estáticos generados previamente.
 * </p>
 */
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