package nl.uscki.appcki.android.fragments.media.helpers.view;

import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.api.MediaAPI;

public abstract class FullScreenMediaView {

    protected Intent intent;
    protected FullScreenMediaActivity activity;

    protected String title;
    protected String transitionNameTemplate;

    private boolean helpersVisible = true;

    public boolean isHelpersVisible() {
        return helpersVisible;
    }

    public void setHelpersVisible(boolean helpersVisible) {
        this.helpersVisible = helpersVisible;
    }

    @Nullable protected int collectionId; // TODO Can be null, should it be here?

    protected FullScreenMediaView(FullScreenMediaActivity activity, Intent intent) {
        this.activity = activity;
        this.transitionNameTemplate = intent.getStringExtra(FullScreenMediaActivity.ARGS_TRANSITION_NAME_TEMPLATE);
        this.title = intent.getStringExtra(FullScreenMediaActivity.ARGS_TITLE);

        this.collectionId = intent.getIntExtra(FullScreenMediaActivity.ARGS_IMAGES_COLLECTION_ID, -1);
    }

    public void init() {
        setupView();
        initToolbar();
    }

    public abstract Toolbar getToolbar();

    protected abstract void setupView();

    public String getApiUrl(MediaAPI.MediaSize size) {
        return getApiUrl(getCurrentAdapterPosition(), size);
    }

    public abstract String getApiUrl(int position, MediaAPI.MediaSize size);

    public String getTitle() {
        return this.title;
    }

    private void initToolbar() {
        if(getToolbar() != null) {
            getToolbar().setVisibility(View.VISIBLE);
            this.activity.setSupportActionBar(getToolbar());
        }
        if(this.activity.getSupportActionBar() != null) {
            this.activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.activity.getSupportActionBar().setHomeButtonEnabled(true);
            this.activity.getSupportActionBar().setTitle(this.title);
        }
    }

    public abstract ImageViewHolder getCurrentImage();

    public abstract String getCurrentImageLink();

    public FullScreenMediaActivity getActivity() {
        return activity;
    }

    public void showToolbar() {
        if(getToolbar() != null) {
            getToolbar().animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0f);
        }
    }

    public void hideToolbar() {
        if(getToolbar() != null) {
            getToolbar().animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0f - getToolbar().getHeight());
        }
    }

    /**
     * This method will be called when the activity is destroyed. Override to implement cleanup
     * behavior.
     */
    public void destroy() {

    }

    /**
     * This method will be called when the activity is exited. Override to implement cleanup
     * behavior
     */
    public void cleanup() {

    }

    /**
     * This method will be called after an image has loaded. Override if post-loading code
     * execution is required.
     * 
     * @param position  Position of the calling class in the image item adapter. -1 if this image
     *                  is not a member of an adapter
     *
     * @return True iff this method is consuming. If this method is not consuming, the enter transition
     * will be started by the calling class
     */
    public boolean afterResourceReady(int position) {
        return false;
    }

    public int getCurrentAdapterPosition() {
        return -1;
    }

    public boolean canNavigateCollection() {
        return false;
    }
}