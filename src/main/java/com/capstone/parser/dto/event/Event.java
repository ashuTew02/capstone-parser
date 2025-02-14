package com.capstone.parser.dto.event;

import com.capstone.parser.model.EventType;

public interface Event<T> {
    String getEventId();
    EventType getType();
    T getPayload();
}
