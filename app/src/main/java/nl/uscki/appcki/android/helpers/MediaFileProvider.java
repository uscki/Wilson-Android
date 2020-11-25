package nl.uscki.appcki.android.helpers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

public class MediaFileProvider extends FileProvider {
    @Override
    public String getType(@NonNull Uri uri) {
        Log.e("MediaFIleProvider", "Found type " + super.getType(uri));
        return "image/jpeg";
//        return super.getType(uri); // TODO https://github.com/bumptech/glide/issues/459
    }
}
