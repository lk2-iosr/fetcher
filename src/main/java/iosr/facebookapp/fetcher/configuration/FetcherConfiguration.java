package iosr.facebookapp.fetcher.configuration;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import iosr.facebookapp.fetcher.FetcherApplication;
import iosr.facebookapp.fetcher.aws.Topic;
import iosr.facebookapp.fetcher.clients.Facebook;
import iosr.facebookapp.fetcher.scheduled.ScheduledFetcher;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.google.common.base.Splitter;

public class FetcherConfiguration extends Configuration {
    private final EnvVars envVars;

    public FetcherConfiguration() {
        this.envVars = new EnvVars();
    }

    public void runScheduledFetcher(final Environment environment) {
        final Map<String, String> pages = createPagesMap();
        final ScheduledFetcher scheduledPostsFetcher = new ScheduledFetcher(getFacebookClient(environment),
                pages,
                Integer.parseInt(this.envVars.getOptional("FETCHER_INTERVAL_IN_MINUTES").orElse("5")));
        environment.lifecycle().manage(scheduledPostsFetcher);
    }

    private Map<String, String> createPagesMap() {
        return Splitter.on(',')
                .trimResults()
                .withKeyValueSeparator(':')
                .split(this.envVars.getRequired("PAGES"));
    }

    private Facebook getFacebookClient(final Environment environment) {
        final WebTarget facebook = getFacebookWebTarget(environment);
        final int postLimit = Integer.parseInt(this.envVars.getOptional("POSTS_LIMIT").orElse("50"));
        final String facebookOAuthKey = this.envVars.getRequired("FACEBOOK_OAUTH_KEY");
        return new Facebook(facebook, checkPostLimit(postLimit), facebookOAuthKey, getTopic());
    }

    private WebTarget getFacebookWebTarget(final Environment environment) {
        final URI facebookURI = URI.create(this.envVars.getRequired("FACEBOOK_URI"));
        final Client jerseyClient = getJerseyClient(environment);
        return jerseyClient.target(facebookURI);
    }

    private Client getJerseyClient(final Environment environment) {
        final Duration timeout = Duration.parse("30 s");
        final Duration connectionTimeout = Duration.parse("25 s");
        final JerseyClientConfiguration clientConfiguration = new JerseyClientConfiguration();
        clientConfiguration.setTimeout(timeout);
        clientConfiguration.setConnectionTimeout(connectionTimeout);
        return new JerseyClientBuilder(environment).using(clientConfiguration)
                .build(FetcherApplication.class.getName());
    }

    private static int checkPostLimit(final int postLimit) {
        return postLimit <= 100 && postLimit > 0 ? postLimit : 100;
    }

    private Topic getTopic() {
        return new Topic(getAmazonSNS(), this.envVars.getRequired("FETCHED_POSTS_TOPIC"));
    }

    private AmazonSNS getAmazonSNS() {
        final String accessKey = this.envVars.getRequired("AWS_ACCESS_KEY");
        final String secretKey = this.envVars.getRequired("AWS_SECRET_KEY");
        final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        return AmazonSNSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }
}
