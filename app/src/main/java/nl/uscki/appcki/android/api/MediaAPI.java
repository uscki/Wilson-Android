package nl.uscki.appcki.android.api;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.model.LazyHeaderFactory;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * Created by peter on 2/6/16.
 */
public class MediaAPI {
    public static String API_URL = App.getContext().getString(R.string.apiurl) + "media/file/";

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

    public static Uri getMediaUri(int id) {
        return Uri.parse(getMediaUrl(id));
    }

    public static Uri getMediaUri(int id, MediaSize size) {
        return Uri.parse(getMediaUrl(id, size));
    }
}
