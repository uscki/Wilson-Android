package nl.uscki.appcki.android.fragments.media.helpers.view.collection;

import android.content.Intent;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public class FullScreenPagerSmoboView extends FullScreenPagerView {

    public FullScreenPagerSmoboView(FullScreenMediaActivity activity, Intent intent) {
        super(activity, intent);
    }

    @Override
    public void callCollectionMetadata(Callback<Pageable<MediaFileMetaData>> callback, int collectionSize) {
        Services.getInstance().smoboService.photos(collectionId, 0, collectionSize).enqueue(callback);
    }

    @Override
    public boolean canNavigateCollection() {
        return true;
    }
}