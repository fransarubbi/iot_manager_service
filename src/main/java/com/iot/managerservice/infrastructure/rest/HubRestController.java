package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.usecase.hub.GetHubsByNetworkUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hubs")
public class HubRestController {

    private final GetHubsByNetworkUseCase getHubsUseCase;

    public HubRestController(GetHubsByNetworkUseCase getHubsUseCase) {
        this.getHubsUseCase = getHubsUseCase;
    }

    @GetMapping
    // Ejemplo de uso desde Frontend: GET /api/hubs?networkId=RED_A
    public ResponseEntity<List<HubSettings>> getHubsByNetwork(@RequestParam String networkId) {
        List<HubSettings> hubs = getHubsUseCase.execute(networkId);
        return ResponseEntity.ok(hubs);
    }
}
