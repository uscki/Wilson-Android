package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.media.MediaFile;

/**
 * Created by peter on 2/5/16.
 */
public class MediaFileEvent {
    public MediaFile file;

    public MediaFileEvent(MediaFile file) {
        this.file = file;
    }
}
