package iosr.facebookapp.fetcher.logging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookHttpStatusEvent extends Event {
    @JsonProperty("status")
    private final int status;
    @JsonProperty("info")
    private final String info;
    @JsonProperty("page")
    private final String page;

    @JsonCreator
    public FacebookHttpStatusEvent(@JsonProperty("eventType") final String eventType,
                                   @JsonProperty("status") final int status,
                                   @JsonProperty("info") final String info,
                                   @JsonProperty("page") final String page) {
        this.eventType = eventType;
        this.status = status;
        this.info = info;
        this.page = page;
    }
}
