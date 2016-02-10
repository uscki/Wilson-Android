package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.MediaCollection;

/**
 * Created by peter on 2/5/16.
 */
public class MediaCollectionReadyEvent {
    public MediaCollection collection;

    public MediaCollectionReadyEvent(MediaCollection collection) {
        this.collection = collection;
    }
}
