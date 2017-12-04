package iosr.facebookapp.fetcher.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Post {
    private static final Logger LOGGER = LoggerFactory.getLogger(Post.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SimpleDateFormat FACEBOOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    @JsonProperty("id")
    private final String id;
    @JsonProperty("message")
    private final String message;
    @JsonProperty("link")
    private final String link;
    @JsonProperty("shares")
    private final int shares;
    @JsonProperty("likes")
    private final int likes;
    @JsonProperty("comments")
    private final int comments;
    @JsonProperty("createdTime")
    @JsonInclude(NON_NULL)
    private final String createdTime;

    private Post(final String id,
                 final String message,
                 final String link,
                 final int shares,
                 final int likes,
                 final int comments,
                 final String createdTime) {
        this.id = id;
        this.message = message;
        this.link = link;
        this.shares = shares;
        this.likes = likes;
        this.comments = comments;
        this.createdTime = createdTime;
    }

    @JsonCreator
    public Post(@JsonProperty("id") final String id,
                @JsonProperty("message") final String message,
                @JsonProperty("link") final String link,
                @JsonProperty("shares") @Nullable final JsonNode shares,
                @JsonProperty("likes") @Nullable final JsonNode likes,
                @JsonProperty("comments") @Nullable final JsonNode comments,
                @JsonProperty("created_time") final String createdTime) {
        this(id, message, link, getSharesCount(shares), getCount(likes), getCount(comments), convertCreatedTime(createdTime));
    }

    private static String convertCreatedTime(final String createdTime) {
        try {
            return FACEBOOK_DATE_FORMAT.parse(createdTime).toInstant().toString();
        }
        catch(final ParseException e) {
            LOGGER.error("Problem with parsing post's created time {}: ", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static int getSharesCount(final JsonNode shares) {
        if(shares.isNull()) {
            return 0;
        }
        return shares.get("count").asInt();
    }


    private static int getCount(final JsonNode data) {
        if(data.isNull()) {
            return 0;
        }
        return data.get("summary").get("total_count").asInt();
    }

    public String asJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        }
        catch(final JsonProcessingException e) {
            LOGGER.error("Problem with writing post as json: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

