package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.news.NewsOverview;

/**
 * Created by peter on 12/30/15.
 */
public class NewsOverviewEvent {
    public NewsOverview newsOverview;

    public NewsOverviewEvent(NewsOverview newsOverview) {
        this.newsOverview = newsOverview;
    }
}
