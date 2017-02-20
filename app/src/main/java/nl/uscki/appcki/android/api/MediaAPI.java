package nl.uscki.appcki.android.api;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * Created by peter on 2/6/16.
 */
public class MediaAPI {
    public static String API_URL = App.getContext().getString(R.string.apiurl) + "media/";

    public enum MediaSize {
        SMALL,
        NORMAL,
        LARGE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static String getMediaUrl(int id) {
        return API_URL + id + "/" + MediaAPI.MediaSize.NORMAL.toString();
    }

    public static String getMediaUrl(int id, MediaSize size) {
        return API_URL + id + "/" + size.toString();
    }
}
