package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.NewsAPI;

/**
 * Created by peter on 12/30/15.
 */
public class NewNewsItemEvent {
    public NewsAPI.NewsItem newsItem;

    public NewNewsItemEvent(NewsAPI.NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
