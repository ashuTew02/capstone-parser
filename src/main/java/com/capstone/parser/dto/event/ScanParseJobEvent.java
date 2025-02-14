package com.capstone.parser.dto.event;

import java.util.UUID;

import com.capstone.parser.dto.event.payload.ScanParseJobEventPayload;
import com.capstone.parser.model.EventType;

public class ScanParseJobEvent implements Event<ScanParseJobEventPayload>{
    private ScanParseJobEventPayload payload;
    private String eventId;

    public ScanParseJobEvent(ScanParseJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public ScanParseJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return EventType.SCAN_PARSE_JOB;
    }

    @Override
    public ScanParseJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
