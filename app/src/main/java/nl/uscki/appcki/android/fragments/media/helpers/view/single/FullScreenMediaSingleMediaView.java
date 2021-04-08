package nl.uscki.appcki.android.fragments.media.helpers.view.single;

import android.content.Intent;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.fragments.media.helpers.view.FullScreenMediaView;
import nl.uscki.appcki.android.fragments.media.helpers.view.ImageViewHolder;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public abstract class FullScreenMediaSingleMediaView extends FullScreenMediaView {

    private Toolbar toolbar;
    protected ViewGroup imageContainer;
    private ImageViewHolder viewHolder;
    protected MediaFileMetaData metaData;

    public FullScreenMediaSingleMediaView(FullScreenMediaActivity activity, Intent intent) {
        super(activity, intent);
    }

    @Override
    protected void setupView() {
        this.activity.setContentView(R.layout.full_screen_media_view);
        this.imageContainer = this.activity.findViewById(R.id.container);
        this.toolbar = this.activity.findViewById(R.id.fullscreen_media_single_image_toolbar);
        this.viewHolder = new ImageViewHolder(this, imageContainer, transitionNameTemplate, metaData);
    }

    @Override
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    public ImageViewHolder getCurrentImage() {
        return this.viewHolder;
    }
}