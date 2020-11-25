package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MediaActivity;
import nl.uscki.appcki.android.fragments.media.MediaOverviewFragment;
import nl.uscki.appcki.android.generated.media.MediaCollection;

public class MediaCollectionAdapter extends BaseItemAdapter<MediaCollectionAdapter.ViewHolder, MediaCollection> {

    private FragmentActivity activity;
    private List<MediaCollection> parentCollections;

    public MediaCollectionAdapter(List<MediaCollection> items, FragmentActivity activity, List<MediaCollection> parentCollections) {
        super(items);
        this.activity = activity;
        this.parentCollections = parentCollections;
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_media_collection_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        final MediaCollection collection = items.get(position);
        holder.collectionNameTextView.setText(collection.name);
        holder.photoCountTextView.setText(String.format(Locale.getDefault(), "%d photos", collection.getNumOfPhotos())); // TODO use string resources
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MediaActivity.class);
                intent.putExtra(MediaOverviewFragment.ARG_COLLECTION_ID, collection.id);
                ArrayList<MediaCollection> newParentCollections = new ArrayList<>(parentCollections);
                newParentCollections.add(items.get(position));
                intent.putParcelableArrayListExtra(MediaOverviewFragment.ARG_COLLECTION_PARENTS, newParentCollections);
                activity.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView collectionNameTextView;
        private TextView photoCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.collectionNameTextView = itemView.findViewById(R.id.collection_name);
            this.photoCountTextView = itemView.findViewById(R.id.collection_photo_count);
        }
    }
}
