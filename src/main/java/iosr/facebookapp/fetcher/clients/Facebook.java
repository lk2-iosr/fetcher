package iosr.facebookapp.fetcher.clients;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Facebook {
    private static final Logger LOGGER = LoggerFactory.getLogger(Facebook.class.getName());
    private final WebTarget facebook;
    private final int postLimit;
    private final String facebookOAuthKey;

    public Facebook(final WebTarget facebook,
                    final int postLimit,
                    final String facebookOAuthKey) {
        this.facebook = facebook;
        this.postLimit = postLimit;
        this.facebookOAuthKey = facebookOAuthKey;
    }

    public void fetchPagePosts(final String id, final String title) {
        Response response = null;
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
        }
        processResponse(response, title);
    }

    private void processResponse(final Response response, final String pageTitle) {

    }
}
