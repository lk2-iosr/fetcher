package iosr.facebookapp.fetcher.clients;

import iosr.facebookapp.fetcher.aws.Topic;
import iosr.facebookapp.fetcher.model.Post;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Facebook {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(Facebook.class.getName());
    private final WebTarget facebook;
    private final int postLimit;
    private final String facebookOAuthKey;
    private final Topic topic;

    public Facebook(final WebTarget facebook,
                    final int postLimit,
                    final String facebookOAuthKey, final Topic topic) {
        this.facebook = facebook;
        this.postLimit = postLimit;
        this.facebookOAuthKey = facebookOAuthKey;
        this.topic = topic;
    }

    public void fetchPagePosts(final String id) {
        Response response;
        try {
            response = this.facebook.path(id).path("posts")
                    .queryParam("limit", this.postLimit)
                    .queryParam("fields",
                            "id,message,shares,link,likes.summary(true),comments.summary(true),created_time")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "OAuth " + this.facebookOAuthKey)
                    .get();
        }
        catch(Exception e) {
            LOGGER.error("Problem with obtaining response: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        processResponse(response);
    }

    private void processResponse(final Response response) {
        final ArrayNode data = (ArrayNode) response.readEntity(JsonNode.class).get("data");
        data.forEach(this::publishPost);
    }

    private void publishPost(final JsonNode p) {
        if(p.has("message") && p.has("link")) {
            try {
                final Post post = OBJECT_MAPPER.treeToValue(p, Post.class);
                this.topic.publish(post);
            }
            catch(JsonProcessingException e) {
                LOGGER.error("Problem with parsing post: {}", e.getMessage());
            }
        }
    }
}
