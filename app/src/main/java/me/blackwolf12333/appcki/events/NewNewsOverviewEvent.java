package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.NewsAPI;

/**
 * Created by peter on 12/30/15.
 */
public class NewNewsOverviewEvent {
    public NewsAPI.NewsOverview newsOverview;

    public NewNewsOverviewEvent(NewsAPI.NewsOverview newsOverview) {
        this.newsOverview = newsOverview;
    }
}
