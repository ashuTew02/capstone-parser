package com.capstone.parser.dto.event.ticket;

import java.util.UUID;

import com.capstone.parser.dto.event.Event;
import com.capstone.parser.dto.event.payload.ticket.TicketUpdateStatusEventPayload;
import com.capstone.parser.model.EventType;

public final class TicketUpdateStatusEvent implements Event<TicketUpdateStatusEventPayload> {
    private TicketUpdateStatusEventPayload payload;
    private String eventId;
    private EventType type = EventType.TICKET_UPDATE_STATUS;


    public TicketUpdateStatusEvent(TicketUpdateStatusEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public TicketUpdateStatusEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public TicketUpdateStatusEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
