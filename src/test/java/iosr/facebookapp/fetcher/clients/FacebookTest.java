package iosr.facebookapp.fetcher.clients;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import iosr.facebookapp.fetcher.aws.Topic;
import iosr.facebookapp.fetcher.model.Post;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyInvocation;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class FacebookTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ID = "123_456";
    private final String LINK = "http://test.com";
    private final String CREATED_TIME = "2017-12-07T13:44:00Z";
    private final int SHARES = 3;
    private final int LIKES = 1;
    private final int COMMENTS = 2;
    private final WebTarget target = mock(WebTarget.class);
    private final Response response = mock(Response.class);
    private Topic topic = mock(Topic.class);
    private Facebook facebook;

    @Before
    public void setUp() throws Exception {
        mockWebTarget();
        this.facebook = new Facebook(this.target, 5, "oauth key", this.topic);
    }

    private void mockWebTarget() {
        final JerseyInvocation.Builder builder = mock(JerseyInvocation.Builder.class);
        when(builder.header(HttpHeaders.AUTHORIZATION, "OAuth oauth key")).thenReturn(builder);
        when(builder.get()).thenReturn(this.response);
        when(this.target.queryParam(anyString(), any())).thenReturn(this.target);
        when(this.target.path(anyString())).thenReturn(this.target);
        when(this.target.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    }

    @Test
    public void shouldPublishAllPosts() throws Exception {
        prepareResponse(ImmutableList.of("post.json",
                "post-without-shares.json",
                "post-without-likes.json",
                "post-without-comments.json"));
        this.facebook.fetchPagePosts("pageId");
        verify(this.topic).publish(new Post(ID, "Something happened!", LINK, SHARES, LIKES, COMMENTS, CREATED_TIME));
        verify(this.topic).publish(new Post(ID, "No shares!", LINK, 0, LIKES, COMMENTS, CREATED_TIME));
        verify(this.topic).publish(new Post(ID, "No likes!", LINK, SHARES, 0, COMMENTS, CREATED_TIME));
        verify(this.topic).publish(new Post(ID, "No comments!", LINK, SHARES, LIKES, 0, CREATED_TIME));
    }

    @Test
    public void shouldNotPublishAnyPost() throws Exception {
        prepareResponse(ImmutableList.of("post-without-message.json", "post-without-link.json"));
        this.facebook.fetchPagePosts("pageId");
        verifyZeroInteractions(this.topic);
    }

    @Test
    public void shouldNotPublishPostWithoutLink() throws Exception {
        prepareResponse(ImmutableList.of("post.json", "post-without-link.json"));
        this.facebook.fetchPagePosts("pageId");
        verify(this.topic).publish(new Post(ID, "Something happened!", LINK, SHARES, LIKES, COMMENTS, CREATED_TIME));
        verifyNoMoreInteractions(this.topic);
    }

    private void prepareResponse(final List<String> resourcesName) throws Exception {
        final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        final List<JsonNode> nodes = resourcesName.stream().map(FacebookTest::createNode).collect(Collectors.toList());
        final ArrayNode arrayNode = jsonNodeFactory.arrayNode().addAll(nodes);
        when(this.response.readEntity(JsonNode.class)).thenReturn(jsonNodeFactory.objectNode().set("data", arrayNode));
    }

    private static JsonNode createNode(final String resourceName) {
        try {
            final String post = getResource("facebook/" + resourceName);
            return OBJECT_MAPPER.readTree(post);
        }
        catch(final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResource(final String resourceName) throws IOException {
        return Resources.asCharSource(Resources.getResource(resourceName), StandardCharsets.UTF_8).read();
    }
}
