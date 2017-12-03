package iosr.facebookapp.fetcher;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import iosr.facebookapp.fetcher.configuration.FetcherConfiguration;

public class FetcherApplication extends Application<FetcherConfiguration>{
    public static void main(final String[] args) throws Exception {
        new FetcherApplication().run(args);
    }

    @Override
    public void run(final FetcherConfiguration configuration, final Environment environment) throws Exception {
        configuration.runScheduledFetcher(environment);
    }
}
