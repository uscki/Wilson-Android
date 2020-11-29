package nl.uscki.appcki.android.fragments.media.helpers.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.igreenwood.loupe.Loupe;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.media.adapters.FullScreenMediaItemAdapter;
import nl.uscki.appcki.android.fragments.media.adapters.ImageTagAdapter;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public class ImageViewHolder {

    private FullScreenMediaView mediaView;
    private @Nullable FullScreenMediaItemAdapter adapter;

    private ImageView imageView;
    private ViewGroup imageContainer;

    private String transitionName;

    private ConstraintLayout helpersContainer;
    private RecyclerView tagView;
    private TextView indexTextView;
    private TextView dateAddedTextView;

    private @Nullable MediaFileMetaData metaData;
    private int fixedAdapterPosition = -1;

    private Loupe loupe;

    private boolean helpersVisible = true;

    private ImageViewHolder(FullScreenMediaView parentMediaView, ViewGroup imageContainer, String transitionName) {
        this.mediaView = parentMediaView;
        this.imageContainer = imageContainer;
        this.transitionName = transitionName;
    }

    // Constructor for viewholder with collection of images
    public ImageViewHolder(
            FullScreenMediaView parentMediaView,
            ViewGroup imageContainer,
            String transitionName,
            @NotNull FullScreenMediaItemAdapter itemAdapter,
            int fixedAdapterPosition,
            @Nullable MediaFileMetaData metaData
    ) {
        this(parentMediaView, imageContainer, transitionName);
        this.adapter = itemAdapter;
        this.fixedAdapterPosition = fixedAdapterPosition;
        this.metaData = metaData;
        initialize();
    }

    // Constructor for single image view
    public ImageViewHolder(
            FullScreenMediaView parentMediaView,
            ViewGroup imageContainer,
            String transitionName,
            @Nullable MediaFileMetaData metaData
    ) {
        this(parentMediaView, imageContainer, transitionName);
        this.metaData = metaData;
        initialize();
    }

    private void initialize() {
        populateViews();
        setupView();
    }

    private void populateViews() {
        this.imageView = this.imageContainer.findViewById(R.id.image);
        this.tagView = this.imageContainer.findViewById(R.id.full_screen_image_tags);
        this.helpersContainer = this.imageContainer.findViewById(R.id.full_screen_image_helpers_container);
        this.dateAddedTextView = this.imageContainer.findViewById(R.id.full_screen_image_date_added);
        this.indexTextView = this.imageContainer.findViewById(R.id.full_screen_image_current_index);
    }

    private void setupView() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this.mediaView.getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        this.tagView.setLayoutManager(layoutManager);

        if(this.metaData != null) {
            tagView.setAdapter(new ImageTagAdapter(this.mediaView.getActivity(), this.metaData.getTags()));
            dateAddedTextView.setText("Toegevoegd op " + this.metaData.getCollection().getDate().toString("d MMMM Y")); // TODO string resources
        } else {
            this.helpersContainer.setVisibility(View.GONE);
        }

        TextView currentIndexTextView = imageContainer.findViewById(R.id.full_screen_image_current_index);
        if(adapter != null) {
            currentIndexTextView.setText(String.format(Locale.getDefault(), "%d / %d", this.fixedAdapterPosition + 1, adapter.getCount())); // TODO string resources
        } else {
            currentIndexTextView.setText("");
        }

        View helpersContainer = imageContainer.findViewById(R.id.full_screen_image_helpers_container);
        helpersContainer.setOnClickListener(v ->
                changeUiVisibility(!helpersVisible));

        this.mediaView.getRequestBuilder(this.fixedAdapterPosition)
                .fitCenter()
                .listener(glideRequestListener)
                .into(imageView);
    }

    public void changeUiVisibility(boolean visible) {
        if(visible) {
            if(this.indexTextView != null)
                this.indexTextView.animate().translationX(0).setDuration(500);
            if(this.tagView != null)
                this.tagView.animate().translationY(0).setDuration(500);
            if(this.dateAddedTextView != null)
                this.dateAddedTextView.animate().translationX(0).setDuration(500);
            this.mediaView.showToolbar();
        } else {
            if(this.indexTextView != null)
                this.indexTextView.animate().translationX(this.mediaView.getActivity().getWindow().getDecorView().getWidth() - indexTextView.getLeft()).setDuration(500);
            if(this.tagView != null)
                this.tagView.animate().translationY(this.mediaView.getActivity().getWindow().getDecorView().getHeight() - tagView.getTop()).setDuration(500);
            if(this.dateAddedTextView != null)
                this.dateAddedTextView.animate().translationX(-1 * dateAddedTextView.getRight()).setDuration(500);
            this.mediaView.hideToolbar();
        }
        this.helpersVisible = visible;
    }

    protected RequestListener<Drawable> glideRequestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            mediaView.getActivity().startPostponedEnterTransition();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            imageView.setTransitionName(transitionName);
            ImageViewHolder.this.loupe = createLoupe();
            if(!mediaView.afterResourceReady(fixedAdapterPosition)) {
                mediaView.getActivity().startPostponedEnterTransition();
            }
            return false;
        }
    };

    private Loupe createLoupe() {
        Loupe loupe = new Loupe(imageView, imageContainer);
        loupe.setUseFlingToDismissGesture(this.transitionName == null);
        loupe.setOnScaleChangedListener((v, v1, v2) -> {
            changeUiVisibility(v <= 1.4);
        });
        loupe.setOnViewTranslateListener(new Loupe.OnViewTranslateListener() {
            @Override
            public void onStart(@NotNull ImageView imageView) {
                changeUiVisibility(false);
            }

            @Override
            public void onViewTranslate(@NotNull ImageView imageView, float v) {
            }

            @Override
            public void onDismiss(@NotNull ImageView imageView) {
                mediaView.getActivity().goBackToPhotoOverview();
            }

            @Override
            public void onRestore(@NotNull ImageView imageView) {
                changeUiVisibility(true);
            }
        });
        return loupe;
    }

    public void cleanup() {
        if(this.loupe != null)
            this.loupe.cleanup();
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ViewGroup getImageContainer() {
        return imageContainer;
    }

    @Nullable
    public MediaFileMetaData getMetaData() {
        return metaData;
    }

    public boolean hasCollection() {
        return this.metaData != null && this.metaData.getCollection() != null;
    }

    public void setMetadata(MediaFileMetaData metadata) {
        this.metaData = metadata;
    }
}