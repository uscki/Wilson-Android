package nl.uscki.appcki.android.fragments.media.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.media.helpers.view.ImageViewHolder;
import nl.uscki.appcki.android.fragments.media.helpers.view.collection.FullScreenPagerView;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import retrofit2.Response;

public class FullScreenMediaItemAdapter extends PagerAdapter {

    private FullScreenPagerView collectionView;
    private final int size;
    private String transitionNameTemplate;

    private int currentPosition;
    private MediaFileMetaData[] media;
    private ImageViewHolder[] imageViews;

    public FullScreenMediaItemAdapter(FullScreenPagerView collectionView, String transitionNameTemplate) {
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

    private Callback<Pageable<MediaFileMetaData>> fullCollectionCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            if(response.body() != null) {
                List<MediaFileMetaData> metaDataList = response.body().getContent();
                if(metaDataList != null) {
                    for(int i = 0; i < metaDataList.size(); i++) {
                        media[i] = metaDataList.get(i);
                        if(imageViews[i] != null)
                            imageViews[i].setMetadata(metaDataList.get(i));
                    }
                }
            }
        }
    };

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup imageContainer;
        if(imageViews[position] != null) {
            imageContainer = imageViews[position].getImageContainer();
        } else {
            imageContainer = (ViewGroup) LayoutInflater.from(collectionView.getActivity()).inflate(R.layout.full_screen_media_view, container, false);
        }
        container.addView(imageContainer);

        ImageViewHolder imageViewHolder = new ImageViewHolder(this.collectionView, imageContainer, transitionNameTemplate + position, this, position, media[position]);
        imageViews[position] = imageViewHolder;

        return imageContainer;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        this.currentPosition = position;
        collectionView.showToolbar();
        if(imageViews != null && imageViews[position] != null)
            trySetTitle(imageViews[position]);
    }

    private void trySetTitle(ImageViewHolder imageViewHolder) {
        if(collectionView.getToolbar() != null) {
            if(imageViewHolder != null && imageViewHolder.getMetaData() != null &&
                    imageViewHolder.getMetaData().getCollection() != null
            ) {
                this.collectionView.getToolbar().setTitle(imageViewHolder.getMetaData().getCollection().getName());
            } else {
                this.collectionView.getToolbar().setTitle("");
            }
        }
    }

    @Override
    public int getCount() {
        return this.size;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void clear() {
        for(ImageViewHolder imageViewHolder : this.imageViews) {
            if(imageViewHolder != null)
                imageViewHolder.cleanup();
        }
        this.media = null;
        this.imageViews = null;
    }
}
