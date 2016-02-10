package me.blackwolf12333.appcki.events;

import android.graphics.Bitmap;

/**
 * Created by peter on 2/3/16.
 */
public class MediaBitmapEvent {
    public Bitmap bitmap;
    public Integer fileid;

    public MediaBitmapEvent(Integer fileid, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.fileid = fileid;
    }
}
