package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.MediaFile;

/**
 * Created by peter on 2/5/16.
 */
public class MediaFileEvent {
    public MediaFile file;

    public MediaFileEvent(MediaFile file) {
        this.file = file;
    }
}
