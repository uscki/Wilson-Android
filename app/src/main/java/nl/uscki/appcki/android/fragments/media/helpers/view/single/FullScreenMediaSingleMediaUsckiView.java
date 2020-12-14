package nl.uscki.appcki.android.fragments.media.helpers.view.single;

import android.content.Intent;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.media.helpers.MediaActionHelper;

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
        return MediaActionHelper.getImageLink(this.mediaId);
    }

    @Override
    public boolean canNavigateCollection() {
        return getCurrentImage().hasCollection() &&
                getCurrentImage().getMetaData().getParentCollection().getId() != null &&
                getCurrentImage().getMetaData().getParentCollection().getId() >= 0;
    }
}