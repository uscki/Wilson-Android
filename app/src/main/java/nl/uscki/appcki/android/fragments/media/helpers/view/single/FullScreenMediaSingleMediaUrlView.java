package nl.uscki.appcki.android.fragments.media.helpers.view.single;

import android.content.Intent;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.MediaAPI;

public class FullScreenMediaSingleMediaUrlView extends FullScreenMediaSingleMediaView {

    private String imageUrl;

    public FullScreenMediaSingleMediaUrlView(FullScreenMediaActivity activity, Intent intent) {
        super(activity, intent);
        this.imageUrl = activity.getIntent().getStringExtra(FullScreenMediaActivity.ARGS_IMAGE_URL);
    }

    @Override
    public String getApiUrl(int position, MediaAPI.MediaSize size) {
        return this.imageUrl;
    }

    @Override
    public String getCurrentImageLink() {
        return this.imageUrl;
    }
}