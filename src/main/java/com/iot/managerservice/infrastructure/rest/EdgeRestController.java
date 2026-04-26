package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.usecase.edge.GetAllEdgesUseCase;
import com.iot.managerservice.usecase.edge.RegisterEdgeUseCase;
import com.iot.managerservice.usecase.edge.DeleteEdgeUseCase;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST (Adaptador de Entrada/Primario) para la orquestación del ciclo de vida de los Edges.
 * <p>
 * Define la interfaz HTTP (API) utilizada por el dashboard administrativo para añadir nuevos
 * nodos principales a la topología, visualizar los existentes, forzar su eliminación
 * o descargar sus artefactos estáticos preconfigurados.
 * </p>
 */
@RestController
@RequestMapping("/api/edges")
public class EdgeRestController {

    private final RegisterEdgeUseCase registerUseCase;
    private final EdgeConfigExporter configExporter;
    private final DeleteEdgeUseCase deleteUseCase;
    private final GetAllEdgesUseCase getAllUseCase;

    public EdgeRestController(RegisterEdgeUseCase registerUseCase,
                              DeleteEdgeUseCase deleteUseCase,
                              EdgeConfigExporter configExporter,
                              GetAllEdgesUseCase getAllUseCase) {
        this.registerUseCase = registerUseCase;
        this.deleteUseCase = deleteUseCase;
        this.configExporter = configExporter;
        this.getAllUseCase = getAllUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Edge edge) {
        registerUseCase.execute(edge);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadConfig(@PathVariable String id) {
        byte[] zipData = configExporter.getZipConfiguration(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"edge_" + id + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUseCase.execute(id);
        // Retornamos un 204 No Content
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Edge>> getAllEdges() {
        return ResponseEntity.ok(getAllUseCase.execute());
    }
}