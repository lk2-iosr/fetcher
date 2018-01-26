package iosr.facebookapp.fetcher.logging.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Event {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @JsonProperty("eventType")
    String eventType;

    public String asJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        }
        catch(final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

