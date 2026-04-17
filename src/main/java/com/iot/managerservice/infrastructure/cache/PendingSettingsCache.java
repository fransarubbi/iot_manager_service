package com.iot.managerservice.infrastructure.cache;

import com.iot.managerservice.domain.model.HubSettings;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class PendingSettingsCache {

    private final Map<String, PendingSetting> cache = new ConcurrentHashMap<>();
    private record PendingSetting(HubSettings settings, long messageId, long expiresAt) {}

    public void put(String hubId, HubSettings settings, long messageId) {
        long expirationTime = System.currentTimeMillis() + 300_000;
        cache.put(hubId, new PendingSetting(settings, messageId, expirationTime));
    }

    public Optional<HubSettings> getAndRemoveIfValid(String hubId, long incomingMsgId) {
        PendingSetting pending = cache.get(hubId);

        if (pending == null) return Optional.empty();

        if (System.currentTimeMillis() > pending.expiresAt()) {
            cache.remove(hubId);
            return Optional.empty();
        }

        if (pending.messageId() == incomingMsgId) {
            cache.remove(hubId);
            return Optional.of(pending.settings());
        }

        return Optional.empty();
    }
}
