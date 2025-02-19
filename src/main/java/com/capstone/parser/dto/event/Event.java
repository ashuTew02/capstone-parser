package com.capstone.parser.dto.event;

import com.capstone.parser.model.EventType;

public interface Event<T> {
    EventType getType();
    T getPayload();
    String getEventId();
}
