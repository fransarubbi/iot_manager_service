package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Edge;
import com.iot.managerservice.usecase.edge.RegisterEdgeUseCase;
import com.iot.managerservice.usecase.edge.DeleteEdgeUseCase;
import com.iot.managerservice.domain.port.EdgeConfigExporter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/edges")
public class EdgeRestController {

    private final RegisterEdgeUseCase registerUseCase;
    private final EdgeConfigExporter configExporter;
    private final DeleteEdgeUseCase deleteUseCase;

    public EdgeRestController(RegisterEdgeUseCase registerUseCase,
                              DeleteEdgeUseCase deleteUseCase,
                              EdgeConfigExporter configExporter) {
        this.registerUseCase = registerUseCase;
        this.deleteUseCase = deleteUseCase;
        this.configExporter = configExporter;
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
}