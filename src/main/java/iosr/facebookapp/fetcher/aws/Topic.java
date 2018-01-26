package iosr.facebookapp.fetcher.aws;

import iosr.facebookapp.fetcher.model.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;

public class Topic {
    private static final Logger LOGGER = LoggerFactory.getLogger(Topic.class.getName());
    private final AmazonSNS sns;
    private final String arn;

    public Topic(final AmazonSNS sns, final String arn) {
        this.sns = sns;
        this.arn = arn;
    }

    public void publish(final Post post) {
        final PublishRequest publishRequest = new PublishRequest(this.arn, post.asJson());
        try {
            this.sns.publish(publishRequest);
        }
        catch(final AmazonClientException e) {
            LOGGER.error("Publishing to Amazon SNS failed!");
            throw new RuntimeException(e);
        }
    }
}
