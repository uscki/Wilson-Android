package nl.uscki.appcki.android.fragments.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.media.MediaCollectionFragment;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaCollectionMember;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public class MediaCollectionAdapter extends BaseItemAdapter<MediaCollectionAdapter.MediaCollectionMemberViewHolder, MediaCollectionMember> {

    private Activity activity;
    private MediaCollectionFragment fragment;
    private MediaCollection currentCollection;
    private List<MediaCollection> childCollections;
    private List<MediaFileMetaData> childMediaFiles = new ArrayList<>();

    private List<MediaCollection> parentCollections;

    public MediaCollectionAdapter(Activity activity, MediaCollectionFragment fragment, MediaCollection currentCollection, List<MediaCollection> items, List<MediaCollection> parentCollections) {
        super(new ArrayList<>());
        this.activity = activity;
        this.fragment = fragment;
        this.currentCollection = currentCollection;
        this.childCollections = items;
        this.parentCollections = parentCollections;
    }

    public void addChildCollections(List<MediaCollection> mediaCollectionList) {
        this.childCollections.addAll(mediaCollectionList);
        notifyDataSetChanged();
    }

    public void addMediaFiles(List<MediaFileMetaData> metaDataList) {
        this.childMediaFiles.addAll(metaDataList);
        notifyDataSetChanged();
    }

    public List<MediaCollection> getCollections() {
        return this.childCollections;
    }

    public List<MediaFileMetaData> getFiles() {
        return this.childMediaFiles;
    }

    @Override
    public void clear() {
        this.childCollections = new ArrayList<>();
        this.childMediaFiles = new ArrayList<>();
        super.clear();
    }

    @Override
    public MediaCollectionMemberViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        int resource = viewType == 3 ? R.layout.fragment_media_collection_list_item : R.layout.media_photo_grid_item;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        if(viewType == 3) {
            return new MediaCollectionViewHolder(view);
        } else {
            return new MediaFileMetaDataViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return this.childCollections.size() + this.childMediaFiles.size();
    }

    @Override
    public void onBindCustomViewHolder(MediaCollectionMemberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {
        // This disables the workings of list headers and dividers
        if(position < this.childCollections.size()) {
            return 3;
        } else {
            return 4;
        }
    }

    public class MediaCollectionViewHolder extends MediaCollectionMemberViewHolder {

        private TextView collectionNameTextView;
        private TextView photoCountTextView;

        public MediaCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.collectionNameTextView = itemView.findViewById(R.id.collection_name);
            this.photoCountTextView = itemView.findViewById(R.id.collection_photo_count);
        }

        void bind(int position) {
            final MediaCollection collection = childCollections.get(position);
            this.collectionNameTextView.setText(collection.name);
            this.photoCountTextView.setText(String.format(Locale.getDefault(), "%d photos", collection.getNumOfPhotos())); // TODO use string resources
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) this.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            this.itemView.setLayoutParams(layoutParams);

            this.itemView.setOnClickListener(v -> {
                Bundle arguments = new Bundle();
                arguments.putInt(MediaCollectionFragment.ARG_COLLECTION_ID, collection.getId());
                ArrayList<MediaCollection> newParentCollections = new ArrayList<>(parentCollections);
                newParentCollections.add(collection);
                arguments.putParcelableArrayList(MediaCollectionFragment.ARG_COLLECTION_PARENTS, newParentCollections);

                MediaCollectionFragment childOverviewFragment = new MediaCollectionFragment();
                OpenFragmentEvent event = new OpenFragmentEvent(childOverviewFragment, arguments);
                EventBus.getDefault().post(event);
            });
        }
    }

    public class MediaFileMetaDataViewHolder extends MediaCollectionMemberViewHolder implements View.OnClickListener {

        MediaFileMetaData photoData;
        ImageView photoView;

        public MediaFileMetaDataViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.media_grid_photo);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            Intent intent = new FullScreenMediaActivity
                    .CollectionIntentBuilder(currentCollection.name, "media_browser_")
                    .collectionID(currentCollection.getId())
                    .initialPosition(getMediaFileItemPosition(photoData.getId()), childMediaFiles)
                    .build(itemView.getContext());

            if(activity instanceof BasicActivity) {
                ((MainActivity)activity).registerSharedElementCallback(fragment);
            }

            itemView.getContext().startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity, photoView, photoView.getTransitionName()).toBundle());
        }

        void bind(int position) {
            Drawable placeholder = ContextCompat.getDrawable(itemView.getContext(), R.drawable.animated_uscki_logo_black);
            if(placeholder instanceof Animatable) {
                ((Animatable)placeholder).start();
            }
            this.photoData = childMediaFiles.get(getPositionInMediaList(position));
            this.photoView.setTransitionName("media_browser_" + getPositionInMediaList(position));
            Glide.with(itemView.getContext())
                    .load(MediaAPI.getMediaUrl(this.photoData.getId(), MediaAPI.MediaSize.NORMAL))
                    .fitCenter()
                    .thumbnail(Glide.with(itemView.getContext()).load(MediaAPI.getMediaUri(this.photoData.getId(), MediaAPI.MediaSize.SMALL)))
                    .placeholder(placeholder)
                    .error(R.drawable.ic_wilson)
                    .into(this.photoView);
        }
    }

    public int getPositionInMediaList(int itemPosition) {
        return itemPosition - childCollections.size();
    }

    public int getMediaFileItemPosition(int mediaId) {
        for(int i = 0; i < this.childMediaFiles.size(); i++) {
            if(this.childMediaFiles.get(i).getId() == mediaId)
                return i;
        }
        return -1;
    }

    public static abstract class MediaCollectionMemberViewHolder extends RecyclerView.ViewHolder {
        public MediaCollectionMemberViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(int position);
    }
}
