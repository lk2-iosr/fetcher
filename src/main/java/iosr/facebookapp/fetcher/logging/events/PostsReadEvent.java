package iosr.facebookapp.fetcher.logging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostsReadEvent extends Event {
    @JsonProperty("page")
    private final String page;

    @JsonCreator
    public PostsReadEvent(@JsonProperty("eventType") final String eventType,
                          @JsonProperty("page") final String page) {
        this.eventType = eventType;
        this.page = page;
    }

}
