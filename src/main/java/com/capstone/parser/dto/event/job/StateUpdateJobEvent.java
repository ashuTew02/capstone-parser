package com.capstone.parser.dto.event.job;

import java.util.UUID;

import com.capstone.parser.dto.event.Event;
import com.capstone.parser.dto.event.payload.job.StateUpdateJobEventPayload;
import com.capstone.parser.model.EventType;

public final class StateUpdateJobEvent implements Event<StateUpdateJobEventPayload> {
    private StateUpdateJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.STATE_UPDATE_JOB;


    public StateUpdateJobEvent(StateUpdateJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    
    public StateUpdateJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }


    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public StateUpdateJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
