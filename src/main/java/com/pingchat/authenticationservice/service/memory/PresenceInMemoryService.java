package com.pingchat.authenticationservice.service.memory;

import com.pingchat.authenticationservice.model.event.PresenceEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PresenceInMemoryService {
    private final Map<String, PresenceEvent> presences = new HashMap<>();

    public PresenceEvent getPresence(String userPhoneNumber) {
        return presences.get(userPhoneNumber);
    }

    public List<PresenceEvent> getPresences(List<String> phoneNumbers) {
        return phoneNumbers.stream().map(presences::get)
                .collect(Collectors.toList());
    }

    public void setPresence(PresenceEvent presenceEvent) {
        presenceEvent.setEventTimestamp(Instant.now().toEpochMilli());
        presences.put(presenceEvent.getUserPhoneNumber(), presenceEvent);
    }

}
