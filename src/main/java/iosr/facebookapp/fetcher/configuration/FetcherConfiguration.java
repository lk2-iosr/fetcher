package iosr.facebookapp.fetcher.configuration;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import iosr.facebookapp.fetcher.FetcherApplication;
import iosr.facebookapp.fetcher.clients.Facebook;
import iosr.facebookapp.fetcher.scheduled.ScheduledFetcher;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.google.common.base.Splitter;

public class FetcherConfiguration extends Configuration {
    private final Map<String, String> env;

    public FetcherConfiguration() {
        this.env = System.getenv();
    }

    public void runScheduledFetcher(final Environment environment) {
        final Map<String, String> pages = createPagesMap();
        final ScheduledFetcher scheduledPostsFetcher = new ScheduledFetcher(getFacebookClient(environment),
                pages,
                Integer.parseInt(this.env.get("FETCHER_INTERVAL_IN_MINUTES")));
        environment.lifecycle().manage(scheduledPostsFetcher);
    }

    private Map<String, String> createPagesMap() {
        return Splitter.on(',')
                .trimResults()
                .withKeyValueSeparator(':')
                .split(this.env.get("PAGES"));
    }

    private Facebook getFacebookClient(final Environment environment) {
        final WebTarget facebook = getFacebookWebTarget(environment);
        final int postLimit = 25;
        final String facebookOAuthKey = this.env.get("FACEBOOK_OAUTH_KEY");
        return new Facebook(facebook, checkPostLimit(postLimit), facebookOAuthKey);
    }

    private WebTarget getFacebookWebTarget(final Environment environment) {
        final URI facebookURI = URI.create(this.env.get("FACEBOOK_URI"));
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
        return postLimit <= 100 && postLimit > 0 ? postLimit : 25;
    }

}
