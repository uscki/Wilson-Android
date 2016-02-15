package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.news.NewsItem;

/**
 * Created by peter on 12/30/15.
 */
public class NewsItemEvent {
    public NewsItem newsItem;

    public NewsItemEvent(NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
