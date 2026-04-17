package com.iot.managerservice.infrastructure.rest;

import com.iot.managerservice.domain.model.HubSettings;
import com.iot.managerservice.usecase.hub.GetHubsByNetworkUseCase;
import com.iot.managerservice.usecase.settings.SendNewHubSettingsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hubs")
public class HubRestController {

    private final GetHubsByNetworkUseCase getHubsUseCase;
    private final SendNewHubSettingsUseCase sendNewHubSettingsUseCase;

    public HubRestController(GetHubsByNetworkUseCase getHubsUseCase,
                             SendNewHubSettingsUseCase sendNewHubSettingsUseCase) {
        this.getHubsUseCase = getHubsUseCase;
        this.sendNewHubSettingsUseCase = sendNewHubSettingsUseCase;
    }

    @GetMapping
    // Ejemplo de uso desde Frontend: GET /api/hubs?networkId=RED_A
    public ResponseEntity<List<HubSettings>> getHubsByNetwork(@RequestParam String networkId) {
        List<HubSettings> hubs = getHubsUseCase.execute(networkId);
        return ResponseEntity.ok(hubs);
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<Void> sendNewSettings(@PathVariable String id, @RequestBody HubSettings settings) {
        sendNewHubSettingsUseCase.execute(id, settings);
        return ResponseEntity.accepted().build();
    }
}