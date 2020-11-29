package nl.uscki.appcki.android.fragments.media.helpers;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.fragments.media.helpers.view.FullScreenMediaView;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public class MediaActionHelper {

    private FullScreenMediaActivity activity;
    private FullScreenMediaView mediaView;

    public MediaActionHelper(FullScreenMediaActivity activity, FullScreenMediaView mediaView) {
        this.activity = activity;
        this.mediaView = mediaView;
    }

    /**
     * Create a share intent for the image
     */
    public void shareMedia() {
        Glide.with(this.activity)
                .downloadOnly()
                .load(this.mediaView.getApiUrl())
                .into(shareMediaCallback);
    }

    /**
     * Create a share intent for the URL of the image
     */
    public void shareUrl() {
        Intent shareIntent = createBasicMediaShareIntent();
        shareIntent.putExtra(Intent.EXTRA_TEXT, mediaView.getCurrentImageLink());
        shareIntent.setType("text/*");
        this.activity.startActivity(Intent.createChooser(shareIntent, "Send to...")); // TODO string resource
    }

    public void writeMediaIfPermitted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FullScreenMediaActivity.REQUEST_STORAGE_PERMISSION);
                return;
            }
        }
        Glide.with(this.activity)
                .asBitmap()
                .load(this.mediaView.getApiUrl())
                .into(storeMediaCallback);
    }

    public static String getImageLink(int collectionId, int mediaFileId) {
        return String.format(Locale.getDefault(), "https://www.uscki.nl/?pagina=Media/Archive&subcollection=%d&mediafile=%d", collectionId, mediaFileId);
    }

    public static String getImageLink(int mediaFileId) {
        return String.format(Locale.getDefault(), "https://www.uscki.nl/?pagina=Media/FileView&id=%d&size=large", mediaFileId);
    }

    private Intent createBasicMediaShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String collectionName = getCollectionName();
        if(collectionName != null) {
            intent.putExtra(Intent.EXTRA_TITLE, String.format(
                    Locale.getDefault(), "Foto uit %s", collectionName // TODO string resource
            ));
            intent.putExtra(Intent.EXTRA_SUBJECT, collectionName);
        } else {
            intent.putExtra(Intent.EXTRA_TITLE, "Foto van uscki.nl"); // TODO
            intent.putExtra(Intent.EXTRA_SUBJECT, "Foto van uscki.nl"); // TODO
        }
        intent.putExtra(Intent.EXTRA_TEXT, this.mediaView.getCurrentImageLink()); // TODO extract from current view helper
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private String getCollectionName() {
        String collectionName = null;
        MediaFileMetaData metaData = mediaView.getCurrentImage().getMetaData();
        if(metaData != null && metaData.getCollection() != null) {
            collectionName = metaData.getCollection().name;
        }
        return collectionName;
    }

    CustomTarget<Bitmap> storeMediaCallback = new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + activity.getString(R.string.app_external_media_subdir); // TODO seperate by collection?

            ContentValues values = new ContentValues();
            String collectionName = getCollectionName();
            if(collectionName != null) {
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, collectionName.trim().replaceAll(" ", "_") + "_" + System.currentTimeMillis()); // TODO
            }
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg"); // TODO make general
            values.put(MediaStore.MediaColumns.IS_DOWNLOAD, 1);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.OWNER_PACKAGE_NAME, activity.getPackageName());
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
                values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            }

            ContentResolver resolver = activity.getContentResolver();
            Uri output = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                if(output == null) throw new IOException("Failed to create a MediaStore record");
                OutputStream os = resolver.openOutputStream(output);
                if (os == null) throw new IOException("Failed to create the output stream");

                if (!resource.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                    throw new IOException("Failed to save the bitmap");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(output, values, null, null);
                }
                Log.e("StoreImage", "Image saved in " + output);
                Toast.makeText(activity, String.format(Locale.getDefault(), "Abeelding opgeslagen in %s%s%s", Environment.DIRECTORY_PICTURES, File.separator, activity.getString(R.string.app_external_media_subdir)), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                if(output != null) {
                    resolver.delete(output, null, null);
                }
                Toast.makeText(activity, "Opslaan van afbeelding mislukt", Toast.LENGTH_LONG).show(); // TODO string resource
            }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };

    private CustomTarget<File> shareMediaCallback = new CustomTarget<File>() {
        @Override
        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
            String[] mimeTypeArray = new String[] { "image/jpeg"}; // TODO extract mime type
            Intent intent = createBasicMediaShareIntent();
            intent.setType("image/jpeg");

            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", resource);;

            intent.setClipData(new ClipData(
                    "Foto van U.S.C.K.I. Incognito uit de media collectie \"collectienaam\" (" + mediaView.getCurrentImageLink() + ")", // TODO
                    mimeTypeArray,
                    new ClipData.Item(uri)
            ));

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(intent, "Intent title")); // TODO
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };
}