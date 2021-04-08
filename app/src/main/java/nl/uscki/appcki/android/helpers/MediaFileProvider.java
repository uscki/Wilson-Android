package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaFileProvider extends FileProvider {

    public enum MIME_TYPE {
        JPEG("image/jpeg", Bitmap.CompressFormat.JPEG),
        PNG("image/png", Bitmap.CompressFormat.PNG),
        GIF("image/gif", Bitmap.CompressFormat.PNG),
        WEB_PICTURE_FORMAT("image/webp", Bitmap.CompressFormat.WEBP);

        MIME_TYPE(String mimeType, Bitmap.CompressFormat compressFormat) {
            this.mimeType = mimeType;
            this.compressFormat = compressFormat;
        }

        private String mimeType;
        private Bitmap.CompressFormat compressFormat;

        public String getMimeType() {
            return mimeType;
        }

        public Bitmap.CompressFormat getCompressFormat() {
            return compressFormat;
        }
    }

    public static final MIME_TYPE MIME_TYPE_DEFAULT = MIME_TYPE.JPEG;

    @Override
    public String getType(@NonNull Uri uri) {
        return findMimeType(getContext(), uri).getMimeType();
    }

    public static MIME_TYPE findMimeType(File file) {
        try(FileInputStream fis = new FileInputStream(file)) {
            return findMimeType(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageTypeToMime(null);
    }

    public static MIME_TYPE findMimeType(Context context, Uri uri) {
        try (
            InputStream is = context.getContentResolver().openInputStream(uri);
        ) {
            return findMimeType(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageTypeToMime(null);
    }

    public static MIME_TYPE findMimeType(InputStream inputStream) {
        ImageHeaderParser.ImageType imageType = null;
        try {
            if(inputStream != null)
                imageType = new DefaultImageHeaderParser().getType(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageTypeToMime(imageType);
    }

    private static MIME_TYPE imageTypeToMime(ImageHeaderParser.ImageType imageType) {

        if(imageType == null) return MIME_TYPE_DEFAULT;

        switch (imageType) {
            case GIF:
                return MIME_TYPE.GIF;
            case JPEG:
                return MIME_TYPE.JPEG;
            case PNG_A:
            case PNG:
                return MIME_TYPE.PNG;
            case WEBP_A:
            case WEBP:
                return MIME_TYPE.WEB_PICTURE_FORMAT;
            case RAW: // DOes not seem to have one specific mime type. Let's pray!
            case UNKNOWN:
            default:
                return MIME_TYPE_DEFAULT; // Most obvious guess
        }
    }
}
