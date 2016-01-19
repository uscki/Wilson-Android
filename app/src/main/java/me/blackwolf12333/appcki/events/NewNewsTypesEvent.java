package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.NewsAPI;

/**
 * Created by peter on 12/30/15.
 */
public class NewNewsTypesEvent {
    public NewsAPI.NewsType[] newsTypes;

    public NewNewsTypesEvent(NewsAPI.NewsType[] newsTypes) {
        this.newsTypes = newsTypes;
    }
}
