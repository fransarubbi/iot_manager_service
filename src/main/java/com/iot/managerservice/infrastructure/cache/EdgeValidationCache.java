package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.repository.EdgeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Componente de infraestructura que actúa como una caché en memoria (RAM)
 * altamente concurrente para la validación de dispositivos Edge.
 * <p>
 * Su objetivo principal es evitar consultas constantes a la base de datos (I/O)
 * por cada petición gRPC entrante, manteniendo un Set de IDs autorizados.
 * </p>
 */
@Slf4j
@Component
public class EdgeValidationCache {

    /** * Colección concurrente y thread-safe que almacena exclusivamente los IDs
     * de los Edges válidos, optimizando el uso de memoria.
     */
    private final Set<String> validEdgeIds = ConcurrentHashMap.newKeySet();
    private final EdgeRepository edgeRepository;

    public EdgeValidationCache(EdgeRepository edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    /**
     * Tarea de inicialización ejecutada automáticamente por Spring al arrancar el contexto.
     * <p>
     * Realiza un volcado (warm-up) de todos los IDs de Edges existentes desde
     * la base de datos relacional hacia la memoria RAM.
     * </p>
     */
    @PostConstruct
    public void loadInitialData() {
        log.info("Cargando IDs de Edges desde Postgres a la RAM para validación rápida...");
        edgeRepository.findAll().forEach(edge -> {
            validEdgeIds.add(edge.edgeId());
        });
        log.info("Se cargaron {} Edges en RAM.", validEdgeIds.size());
    }

    /**
     * Verifica de forma casi instantánea si un Edge tiene autorización para operar.
     *
     * @param edgeId El identificador del Edge a consultar.
     * @return {@code true} si el ID existe en la caché, {@code false} en caso contrario.
     */
    public boolean isValid(String edgeId) {
        return validEdgeIds.contains(edgeId);
    }

    /**
     * Registra un nuevo Edge en la caché de memoria.
     * <p>Debe ser invocado tras persistir exitosamente un nuevo Edge en la base de datos.</p>
     *
     * @param edgeId Identificador del nuevo Edge autorizado.
     */
    public void addEdgeToRam(String edgeId) {
        validEdgeIds.add(edgeId);
        log.info("Nuevo Edge autorizado en RAM: {}", edgeId);
    }

    /**
     * Revoca la autorización de un Edge eliminándolo de la caché en memoria.
     *
     * @param edgeId Identificador del Edge a remover.
     */
    public void removeEdgeFromRam(String edgeId) {
        validEdgeIds.remove(edgeId);
        log.info("Edge revocado en RAM: {}", edgeId);
    }
}