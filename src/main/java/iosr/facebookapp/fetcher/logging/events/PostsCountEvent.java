package iosr.facebookapp.fetcher.logging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostsCountEvent extends Event {
    @JsonProperty("page")
    private final String page;

    @JsonCreator
    public PostsCountEvent(@JsonProperty("page") final String eventType,
                           @JsonProperty("page") final String page) {
        this.eventType = eventType;
        this.page = page;
    }
}
