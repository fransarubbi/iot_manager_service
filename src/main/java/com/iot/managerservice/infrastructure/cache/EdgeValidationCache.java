package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.repository.EdgeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class EdgeValidationCache {

    // Se usa un Set en lugar de un Map porque solo nos interesa saber si el ID existe
    private final Set<String> validEdgeIds = ConcurrentHashMap.newKeySet();
    private final EdgeRepository edgeRepository;

    public EdgeValidationCache(EdgeRepository edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        log.info("Cargando IDs de Edges desde Postgres a la RAM para validación rápida...");

        // Buscamos todos los Edges en la BD y guardamos solo sus IDs en la RAM
        edgeRepository.findAll().forEach(edge -> {
            validEdgeIds.add(edge.edgeId());
        });

        log.info("Se cargaron {} Edges en RAM.", validEdgeIds.size());
    }

    public boolean isValid(String edgeId) {
        return validEdgeIds.contains(edgeId);
    }

    public void addEdgeToRam(String edgeId) {
        validEdgeIds.add(edgeId);
        log.info("Nuevo Edge autorizado en RAM: {}", edgeId);
    }

    public void removeEdgeFromRam(String edgeId) {
        validEdgeIds.remove(edgeId);
        log.info("Edge revocado en RAM: {}", edgeId);
    }
}