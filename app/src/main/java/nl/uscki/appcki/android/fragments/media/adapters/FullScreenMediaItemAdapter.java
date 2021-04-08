package nl.uscki.appcki.android.fragments.media.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.media.helpers.view.ImageViewHolder;
import nl.uscki.appcki.android.fragments.media.helpers.view.collection.FullScreenPagerView;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import retrofit2.Response;

public class FullScreenMediaItemAdapter extends FragmentStateAdapter {

    private FullScreenPagerView collectionView;
    private final int size;
    private String transitionNameTemplate;

    private MediaFileMetaData[] media;
    private ImageViewHolder[] imageViews;

    public FullScreenMediaItemAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, FullScreenPagerView collectionView, String transitionNameTemplate) {
        super(fragmentManager, lifecycle);
        this.collectionView = collectionView;
        this.size = collectionView.getTotalImages();
        this.transitionNameTemplate = transitionNameTemplate;

        this.media = new MediaFileMetaData[collectionView.getTotalImages()];
        this.imageViews = new ImageViewHolder[collectionView.getTotalImages()];

        if(collectionView.getInitialImage() != null) {
            this.media[collectionView.getInitialPosition()] = collectionView.getInitialImage();
        }
        if(collectionView.getPreviousImage() != null && collectionView.getInitialPosition() > 0) {
            this.media[collectionView.getInitialPosition() - 1] = collectionView.getPreviousImage();
        }
        if(collectionView.getNextImage() != null && collectionView.getInitialPosition() < this.size - 1) {
            this.media[collectionView.getInitialPosition() + 1] = collectionView.getNextImage();
        }

        Callback<Pageable<MediaFileMetaData>> fullCollectionCallback = new Callback<Pageable<MediaFileMetaData>>() {
            @Override
            public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
                if (response.body() != null) {
                    List<MediaFileMetaData> metaDataList = response.body().getContent();
                    if (metaDataList != null) {
                        for (int i = 0; i < metaDataList.size(); i++) {
                            media[i] = metaDataList.get(i);
                            if (imageViews[i] != null)
                                imageViews[i].setMetadata(metaDataList.get(i));
                        }
                    }
                }
            }
        };
        this.collectionView.callCollectionMetadata(fullCollectionCallback, this.size);
    }

    public ImageViewHolder getImageAt(int position) {
        if(position >= 0 && position < this.imageViews.length)
            return this.imageViews[position];
        return null;
    }

    public MediaFileMetaData getMetadataAt(int position) {
        if(position >= 0 && position < this.media.length)
            return this.media[position];
        return null;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new DetailImageViewItemFragment(this, position);
    }

    ImageViewHolder createViewForFragment(LayoutInflater inflater, ViewGroup container, int position) {
        if(this.imageViews[position] != null) {
            container = this.imageViews[position].getImageContainer();
        } else {
            container = (ViewGroup) inflater.inflate(R.layout.full_screen_media_view, container, true);
        }

        ImageViewHolder imageViewHolder = new ImageViewHolder(collectionView, container, transitionNameTemplate + position, FullScreenMediaItemAdapter.this, position, media[position]);
        imageViews[position] = imageViewHolder;

        return imageViewHolder;
    }

    private void trySetTitle(ImageViewHolder imageViewHolder) {
        if(collectionView.getToolbar() != null) {
            if(imageViewHolder != null && imageViewHolder.getMetaData() != null &&
                    imageViewHolder.getMetaData().getParentCollection() != null
            ) {
                this.collectionView.getToolbar().setTitle(imageViewHolder.getMetaData().getParentCollection().getName());
            } else {
                this.collectionView.getToolbar().setTitle("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.size;
    }

    public void clear() {
        for(ImageViewHolder imageViewHolder : this.imageViews) {
            if(imageViewHolder != null)
                imageViewHolder.cleanup();
        }
        this.media = null;
        this.imageViews = null;
    }

    public void updateUIVisbilityForActivateFragments(int currentPosition) {
        for(int i = 0; i < this.imageViews.length; i++) {
            if(i != currentPosition && this.imageViews[i] != null) {
                this.imageViews[i].updateHelperVisibility();
            }
        }
    }

    public static class DetailImageViewItemFragment extends Fragment {

        private int position;
        private FullScreenMediaItemAdapter adapter;
        private ImageViewHolder imageViewHolder;

        public DetailImageViewItemFragment(FullScreenMediaItemAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            this.imageViewHolder = adapter.createViewForFragment(inflater, container, position);
            this.imageViewHolder.getImageContainer().requestApplyInsets();
            return this.imageViewHolder.getImageContainer();
        }

        @Override
        public void onStart() {
            super.onStart();
            imageViewHolder.updateHelperVisibility();
            adapter.trySetTitle(imageViewHolder);

            if(adapter.collectionView.getLastStableInsets() != null && getView() != null) {
                getView().onApplyWindowInsets(adapter.collectionView.getLastStableInsets());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            adapter.trySetTitle(imageViewHolder);
            if(adapter.collectionView.getLastStableInsets() != null && getView() != null) {
                getView().onApplyWindowInsets(adapter.collectionView.getLastStableInsets());
            }
        }
    }
}
