package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.media.MediaCollection;

/**
 * Created by peter on 2/5/16.
 */
public class MediaCollectionReadyEvent {
    public MediaCollection collection;

    public MediaCollectionReadyEvent(MediaCollection collection) {
        this.collection = collection;
    }
}
