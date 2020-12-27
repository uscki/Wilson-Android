package nl.uscki.appcki.android.fragments.media.helpers.view.single;

import android.content.Intent;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.MediaAPI;

public class FullScreenMediaSingleMediaUsckiView extends FullScreenMediaSingleMediaView {

    private int mediaId;

    public FullScreenMediaSingleMediaUsckiView(FullScreenMediaActivity activity, Intent intent) {
        super(activity, intent);
        this.metaData = intent.getParcelableExtra(FullScreenMediaActivity.ARGS_INITIAL_IMAGE_METADATA);
        if(this.metaData == null) {
            this.mediaId = intent.getIntExtra(FullScreenMediaActivity.ARGS_IMAGE_SINGLE_ID, -1);
        } else {
            this.mediaId = this.metaData.getId();
        }
    }

    @Override
    public String getApiUrl(int position, MediaAPI.MediaSize size) {
        return MediaAPI.getMediaUrl(this.mediaId, size);
    }

    @Override
    public String getCurrentImageLink() {
        return activity.getString(R.string.incognito_website_image_url, this.mediaId);
    }

    @Override
    public boolean canNavigateCollection() {
        return getCurrentImage().hasCollection() &&
                getCurrentImage().getMetaData().getParentCollection().getId() != null &&
                getCurrentImage().getMetaData().getParentCollection().getId() >= 0;
    }
}