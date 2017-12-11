package iosr.facebookapp.fetcher.model;

import static java.util.Objects.requireNonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;

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
    private final String createdTime;

    public Post(final String id,
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
        this(id,
                requireNonNull(message),
                requireNonNull(link),
                getSharesCount(shares),
                getCount(likes),
                getCount(comments),
                convertCreatedTime(createdTime));
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

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Post) {
            final Post post = (Post) o;
            return shares == post.shares &&
                    likes == post.likes &&
                    comments == post.comments &&
                    Objects.equals(id, post.id) &&
                    Objects.equals(message, post.message) &&
                    Objects.equals(link, post.link) &&
                    Objects.equals(createdTime, post.createdTime);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, link, shares, likes, comments, createdTime);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("message", message)
                .add("link", link)
                .add("shares", shares)
                .add("likes", likes)
                .add("comments", comments)
                .add("createdTime", createdTime)
                .toString();
    }
}

