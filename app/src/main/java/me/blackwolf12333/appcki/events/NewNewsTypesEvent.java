package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.NewsType;

/**
 * Created by peter on 12/30/15.
 */
public class NewNewsTypesEvent {
    public NewsType[] newsTypes;

    public NewNewsTypesEvent(NewsType[] newsTypes) {
        this.newsTypes = newsTypes;
    }
}
