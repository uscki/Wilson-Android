package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.NewsItem;

/**
 * Created by peter on 12/30/15.
 */
public class NewNewsItemEvent {
    public NewsItem newsItem;

    public NewNewsItemEvent(NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
