package iosr.facebookapp.fetcher.logging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorEvent extends Event {
    @JsonProperty("body")
    private String body;

    @JsonCreator
    public ErrorEvent(@JsonProperty("eventType") final String eventType,
                      @JsonProperty("body") final String body) {
        this.eventType = eventType;
        this.body = body;
    }
}
