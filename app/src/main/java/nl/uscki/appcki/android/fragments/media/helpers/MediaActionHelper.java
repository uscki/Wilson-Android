package nl.uscki.appcki.android.fragments.media.helpers;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.io.OutputStream;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.media.helpers.view.FullScreenMediaView;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.helpers.MediaFileProvider;

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
                .load(this.mediaView.getApiUrl(MediaAPI.MediaSize.LARGE))
                .into(shareMediaCallback);
    }

    /**
     * Create a share intent for the URL of the image
     */
    public void shareUrl() {
        Intent shareIntent = createBasicMediaShareIntent();
        shareIntent.putExtra(Intent.EXTRA_TEXT, mediaView.getCurrentImageLink());
        shareIntent.setType("text/*");
        this.activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.app_general_action_share_intent_text)));
    }

    public void writeMediaIfPermitted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FullScreenMediaActivity.REQUEST_STORAGE_PERMISSION);
                return;
            }
        }
        Glide.with(this.activity)
                .asFile()
                .load(this.mediaView.getApiUrl(MediaAPI.MediaSize.LARGE))
                .into(storeMediaCallback);
    }

    private Intent createBasicMediaShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String collectionName = getCollectionName();
        if(collectionName != null) {
            intent.putExtra(Intent.EXTRA_TITLE,
                    activity.getString(R.string.wilson_media_collection_intent_share_text_extra, collectionName));
            intent.putExtra(Intent.EXTRA_SUBJECT, collectionName);
        } else {
            intent.putExtra(Intent.EXTRA_TITLE, activity.getString(R.string.wilson_media_collection_intent_share_text_extra));
            intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.wilson_media_collection_intent_share_text_extra));
        }
        intent.putExtra(Intent.EXTRA_TEXT, this.mediaView.getCurrentImageLink());
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private String getCollectionName() {
        String collectionName = null;
        MediaFileMetaData metaData = mediaView.getCurrentImage().getMetaData();
        if(metaData != null && metaData.getParentCollection() != null) {
            collectionName = metaData.getParentCollection().name;
        }
        return collectionName;
    }

    CustomTarget<File> storeMediaCallback = new CustomTarget<File>() {
        @Override
        public void onResourceReady(@NonNull File file, @Nullable Transition<? super File> transition) {
            String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + activity.getString(R.string.app_external_media_subdir);

            ContentValues values = new ContentValues();
            String collectionName = getCollectionName();
            if(collectionName != null) {
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, collectionName.trim().replaceAll(" ", "_") + "_" + System.currentTimeMillis());
            }

            Bitmap bitmap = null;
            MediaFileProvider.MIME_TYPE mime = null;

            try (
                    InputStream is1 = mediaView.getActivity().getContentResolver().openInputStream(Uri.fromFile(file));
                    InputStream is2 = mediaView.getActivity().getContentResolver().openInputStream(Uri.fromFile(file));
            ) {
                bitmap = BitmapFactory.decodeStream(is1);
                mime = MediaFileProvider.findMimeType(is2);
            } catch (IOException e) {
                Toast.makeText(activity, activity.getString(R.string.wilson_media_action_save_image_msg_failure), Toast.LENGTH_LONG).show();
                return;
            }

            values.put(MediaStore.MediaColumns.MIME_TYPE, mime.getMimeType());
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

                if (!bitmap.compress(mime.getCompressFormat(), 100, os)) {
                    throw new IOException("Failed to save the bitmap");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(output, values, null, null);
                }
                Log.v("StoreImage", "Image saved in " + output);
                Toast.makeText(activity, activity.getString(R.string.wilson_media_action_save_image_msg_success, Environment.DIRECTORY_PICTURES, File.separator, activity.getString(R.string.app_external_media_subdir)), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                if(output != null) {
                    resolver.delete(output, null, null);
                }
                Toast.makeText(activity, activity.getString(R.string.wilson_media_action_save_image_msg_failure), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };

    private CustomTarget<File> shareMediaCallback = new CustomTarget<File>() {
        @Override
        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
            MediaFileProvider.MIME_TYPE mime_type = MediaFileProvider.findMimeType(resource);

            String[] mimeTypeArray = new String[] { mime_type.getMimeType() }; // TODO extract mime type
            Intent intent = createBasicMediaShareIntent();
            intent.setType(mime_type.getMimeType());

            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", resource);;

            intent.setClipData(new ClipData(
                    getCollectionName() == null ?
                            activity.getString(R.string.wilson_media_collection_intent_share_label,
                            mediaView.getCurrentImageLink()) :
                            activity.getString(R.string.wilson_media_collection_intent_share_label_with_collection,
                            getCollectionName(),
                            mediaView.getCurrentImageLink()),
                    mimeTypeArray,
                    new ClipData.Item(uri)
            ));

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.wilson_media_collection_intent_share_title)));
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };
}