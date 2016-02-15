package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.news.NewsType;

/**
 * Created by peter on 12/30/15.
 */
public class NewsTypesEvent {
    public NewsType[] newsTypes;

    public NewsTypesEvent(NewsType[] newsTypes) {
        this.newsTypes = newsTypes;
    }
}
