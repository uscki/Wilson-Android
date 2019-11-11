package nl.uscki.appcki.android.fragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

/**
 * Created by peter on 3/8/17.
 */

public class SmoboMediaAdapter extends BaseItemAdapter<SmoboMediaAdapter.ViewHolder, MediaFileMetaData> {
    public SmoboMediaAdapter(List<MediaFileMetaData> items) {
        super(items);
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
        holder.photo.setImageURI(MediaAPI.getMediaUri(items.get(position).getId(), MediaAPI.MediaSize.SMALL));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView photo;

        public ViewHolder(View view) {
            super(view);

            photo = view.findViewById(R.id.photo);
        }
    }
}
