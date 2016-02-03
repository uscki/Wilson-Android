package me.blackwolf12333.appcki.events;

import android.graphics.Bitmap;

/**
 * Created by peter on 2/3/16.
 */
public class MediaFileReadyEvent {
    public Bitmap bitmap;
    public Integer id;

    public MediaFileReadyEvent(Integer id, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.id = id;
    }
}
