package iosr.facebookapp.fetcher.scheduled;

import io.dropwizard.lifecycle.Managed;
import iosr.facebookapp.fetcher.clients.Facebook;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AbstractScheduledService;

public class ScheduledFetcher extends AbstractScheduledService implements Managed {
    private final Facebook facebook;
    private final Map<String, String> pages;
    private final int intervalInMinutes;

    public ScheduledFetcher(final Facebook facebook,
                            final Map<String, String> pages,
                            final int intervalInMinutes) {
        this.facebook = facebook;
        this.pages = pages;
        this.intervalInMinutes = intervalInMinutes;
    }

    @Override
    protected void runOneIteration() {
        this.pages.forEach(this.facebook::fetchPagePosts);
    }

    @Override
    protected Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedRateSchedule(0, this.intervalInMinutes, TimeUnit.MINUTES);
    }

    @Override
    public void start() {
        this.startAsync().awaitRunning();
    }

    @Override
    public void stop() {
        this.stopAsync().awaitTerminated();
    }
}
