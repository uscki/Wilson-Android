package nl.uscki.appcki.android.fragments.media.helpers.view.collection;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.view.View;
import android.view.WindowInsets;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.Map;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.media.adapters.FullScreenMediaItemAdapter;
import nl.uscki.appcki.android.fragments.media.helpers.MediaActionHelper;
import nl.uscki.appcki.android.fragments.media.helpers.view.FullScreenMediaView;
import nl.uscki.appcki.android.fragments.media.helpers.view.ImageViewHolder;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public abstract class FullScreenPagerView extends FullScreenMediaView {

    private ViewPager2 viewPager;
    private FullScreenMediaItemAdapter adapter;
    private Toolbar toolbar;

    private MediaFileMetaData initialImage;
    private MediaFileMetaData previousImage;
    private MediaFileMetaData nextImage;

    private WindowInsets lastStableInsets;

    private int initialPosition;
    private int size;

    protected FullScreenPagerView(FullScreenMediaActivity activity, Intent intent) {
        super(activity, intent);
        this.initialImage = intent.getParcelableExtra(FullScreenMediaActivity.ARGS_INITIAL_IMAGE_METADATA);
        this.previousImage = intent.getParcelableExtra(FullScreenMediaActivity.ARGS_PREVIOUS_IMAGE_METADATA);
        this.nextImage = intent.getParcelableExtra(FullScreenMediaActivity.ARGS_NEXT_IMAGE_METADATA);
        this.initialPosition = intent.getIntExtra(FullScreenMediaActivity.ARGS_INITIAL_POSITION, 0);
        this.size = intent.getIntExtra(FullScreenMediaActivity.ARGS_COLLECTION_SIZE, -1);
    }

    @Override
    protected void setupView() {
        this.activity.setContentView(R.layout.full_screen_media_viewpager);
        this.viewPager = this.activity.findViewById(R.id.fullscreen_media_viewpager);
        this.adapter = new FullScreenMediaItemAdapter(activity.getSupportFragmentManager(), activity.getLifecycle(), this, this.transitionNameTemplate);
        this.viewPager.setAdapter(this.adapter);
        this.viewPager.setCurrentItem(this.initialPosition, false);
        this.toolbar = this.activity.findViewById(R.id.fullscreen_media_toolbar);

        ConstraintLayout viewpagerContainer = this.activity.findViewById(R.id.fullscreen_media_viewpager_container);
        View toolbarContainer = this.activity.findViewById(R.id.fullscreen_media_viewpager_toolbar_container);

        // Toolbar container receives system insets of 0 on all sides, so we override the insets
        // passed to it, so they are never applied, and set the padding of the container manually
        // when the parent view receives new insets.
        toolbarContainer.setOnApplyWindowInsetsListener((v, insets) -> WindowInsets.CONSUMED);
        viewpagerContainer.setOnApplyWindowInsetsListener((v, insets) -> {
            toolbarContainer.setPadding(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());

            this.lastStableInsets = insets;
            return insets;
        });
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    public WindowInsets getLastStableInsets() {
        return lastStableInsets;
    }

    @Override
    public String getCurrentImageLink() {
        return MediaActionHelper.getImageLink(this.collectionId, this.adapter.getImageAt(this.viewPager.getCurrentItem()).getMetaData().getId());
    }

    @Override
    public ImageViewHolder getCurrentImage() {
        return this.adapter.getImageAt(this.viewPager.getCurrentItem());
    }

    @Override
    public void cleanup() {
        if(this.adapter != null) this.adapter.clear();
    }

    @Override
    public void destroy() {
        this.adapter = null;
    }

    public MediaFileMetaData getInitialImage() {
        return initialImage;
    }

    public MediaFileMetaData getPreviousImage() {
        return previousImage;
    }

    public MediaFileMetaData getNextImage() {
        return nextImage;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

    public int getTotalImages() {
        return size;
    }

    public abstract void callCollectionMetadata(Callback<Pageable<MediaFileMetaData>> callback, int collectionSize);

    @Override
    public boolean afterResourceReady(int position) {
        if (position == initialPosition) {
            this.activity.setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    if(names == null) return;

                    View view = adapter.getImageAt(viewPager.getCurrentItem()).getImageView();
                    if(viewPager == null || view == null)
                        return;

                    viewPager.setTransitionName(transitionNameTemplate + viewPager.getCurrentItem());
                    if(sharedElements != null) {
                        sharedElements.clear();
                        sharedElements.put(viewPager.getTransitionName(), view);
                    }
                }
            });
            activity.startPostponedEnterTransition();
        }
        return true;
    }

    @Override
    public String getApiUrl(int position, MediaAPI.MediaSize size) {
        MediaFileMetaData metaData = this.adapter.getMetadataAt(position);
        if(metaData != null) {
            return MediaAPI.getMediaUrl(metaData.getId(), size);
        }
        return null;
    }

    @Override
    public int getCurrentAdapterPosition() {
        return this.viewPager.getCurrentItem();
    }

    @Override
    public void setViewpagerEnabled(boolean enabled) {
        this.viewPager.setUserInputEnabled(enabled);
    }
}
