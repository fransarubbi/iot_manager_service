package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.usecase.network.ManageNetworkUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/networks")
public class NetworkRestController {

    private final ManageNetworkUseCase manageNetworkUseCase;

    public NetworkRestController(ManageNetworkUseCase manageNetworkUseCase) {
        this.manageNetworkUseCase = manageNetworkUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createNetwork(@RequestBody Network network) {
        manageNetworkUseCase.createNetwork(network);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PATCH es el método HTTP correcto para modificaciones parciales
    @PatchMapping("/{id}/update")
    public ResponseEntity<Void> updateNetwork(@PathVariable String id) {
        manageNetworkUseCase.updateNetwork(id);
        return ResponseEntity.ok().build();
    }

    // DELETE para el borrado total
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNetwork(@PathVariable String id) {
        manageNetworkUseCase.deleteNetwork(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
