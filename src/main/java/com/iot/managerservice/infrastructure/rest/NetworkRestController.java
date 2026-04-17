package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.Network;
import com.iot.managerservice.domain.model.NetworkSummary;
import com.iot.managerservice.usecase.network.GetFullNetworksByEdgeUseCase;
import com.iot.managerservice.usecase.network.GetNetworksByEdgeUseCase;
import com.iot.managerservice.usecase.network.ManageNetworkUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/networks")
public class NetworkRestController {

    private final ManageNetworkUseCase manageNetworkUseCase;
    private final GetNetworksByEdgeUseCase getNetworksByEdgeUseCase;
    private final GetFullNetworksByEdgeUseCase getFullNetworksUseCase;

    public NetworkRestController(ManageNetworkUseCase manageNetworkUseCase,
                                 GetNetworksByEdgeUseCase getNetworksByEdgeUseCase,
                                 GetFullNetworksByEdgeUseCase getFullNetworksUseCase) {
        this.manageNetworkUseCase = manageNetworkUseCase;
        this.getNetworksByEdgeUseCase = getNetworksByEdgeUseCase;
        this.getFullNetworksUseCase = getFullNetworksUseCase;
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

    @GetMapping
    // Ejemplo de uso desde Frontend: GET /api/networks?edgeId=EDGE_1
    public ResponseEntity<List<NetworkSummary>> getNetworks(@RequestParam String edgeId) {
        List<NetworkSummary> networks = getNetworksByEdgeUseCase.execute(edgeId);
        return ResponseEntity.ok(networks);
    }

    @GetMapping("/detail")
    // Ejemplo: GET /api/networks/detail?edgeId=EDGE_01
    public ResponseEntity<List<Network>> getFullNetworks(@RequestParam String edgeId) {
        return ResponseEntity.ok(getFullNetworksUseCase.execute(edgeId));
    }
}
