package nl.uscki.appcki.android.fragments.adapters;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

/**
 * Created by peter on 3/8/17.
 */

public class SmoboMediaAdapter extends BaseItemAdapter<SmoboMediaAdapter.ViewHolder, MediaFileMetaData> {
    private SmoboActivity activity;
    private int personId;

    Drawable loadingDrawable;

    public SmoboMediaAdapter(SmoboActivity activity, int personId, List<MediaFileMetaData> items) {
        super(items);
        this.activity = activity;
        this.personId = personId;
        this.loadingDrawable = ContextCompat.getDrawable(activity, R.drawable.animated_uscki_logo_black);
        if(loadingDrawable instanceof Animatable)
            ((Animatable)this.loadingDrawable).start();
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.smobo_photo_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        // we can assume that these integers will always be valid media id's
        holder.photo.setTransitionName("smobo_image_" + position);
        Glide.with(holder.itemView.getContext())
                .load(MediaAPI.getMediaUri(items.get(position).getId(), MediaAPI.MediaSize.NORMAL))
                .placeholder(this.loadingDrawable)
                .error(R.drawable.ic_wilson)
                .into(holder.photo);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView photo;

        public ViewHolder(View view) {
            super(view);
            photo = view.findViewById(R.id.photo);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new FullScreenMediaActivity
                    .CollectionIntentBuilder(null, "smobo_image_")
                    .initialPosition(getAdapterPosition(), items)
                    .collectionID(personId)
                    .isSmobo()
                    .build(activity);

            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, photo, photo.getTransitionName()).toBundle());
        }
    }
}
