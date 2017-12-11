package iosr.facebookapp.fetcher.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

public class PostTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ID = "123_456";
    private final String LINK = "http://test.com";
    private final String CREATED_TIME = "2017-12-07T13:44:00Z";
    private final int SHARES = 3;
    private final int LIKES = 1;
    private final int COMMENTS = 2;

    @Test
    public void shouldCorrectlyCreatePostWithAllElements() throws Exception {
        final Post post = facebookPost("post.json");
        final Post expectedPost = new Post(ID, "Something happened!", LINK, SHARES, LIKES, COMMENTS, CREATED_TIME);
        assertThat(post).isEqualTo(expectedPost);
    }

    @Test(expected = JsonMappingException.class)
    public void shouldNotCreatePostFromJsonWithoutLink() throws Exception {
        facebookPost("post-without-link.json");
    }

    @Test(expected = JsonMappingException.class)
    public void shouldNotCreatePostFromJsonWithoutMessage() throws Exception {
        facebookPost("post-without-message.json");
    }

    @Test
    public void shouldCreatePostWithZeroShares() throws Exception {
        final Post post = facebookPost("post-without-shares.json");
        final Post expectedPost = new Post(ID, "No shares!", LINK, 0, LIKES, COMMENTS, CREATED_TIME);
        assertThat(post).isEqualTo(expectedPost);
    }

    @Test
    public void shouldCreatePostWithZeroLikes() throws Exception {
        final Post post = facebookPost("post-without-likes.json");
        final Post expectedPost = new Post(ID, "No likes!", LINK, SHARES, 0, COMMENTS, CREATED_TIME);
        assertThat(post).isEqualTo(expectedPost);
    }

    @Test
    public void shouldCreatePostWithZeroComments() throws Exception {
        final Post post = facebookPost("post-without-comments.json");
        final Post expectedPost = new Post(ID, "No comments!", LINK, SHARES, LIKES, 0, CREATED_TIME);
        assertThat(post).isEqualTo(expectedPost);
    }

    private static Post facebookPost(final String resourceName) throws IOException {
        final String facebookPost = getResource("facebook/" + resourceName);
        return OBJECT_MAPPER.readValue(facebookPost, Post.class);
    }

    private static String getResource(final String resourceName) throws IOException {
        return Resources.asCharSource(Resources.getResource(resourceName), StandardCharsets.UTF_8).read();
    }
}