package nl.uscki.appcki.android.events;

import android.graphics.Rect;

/**
 * Created by peter on 6-9-16.
 */

public class ImageZoomEvent {
    public Integer id;
    public Rect startBounds;

    public ImageZoomEvent(Rect startBounds, Integer id) {
        this.id = id;
        this.startBounds = startBounds;
    }
}
