package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.smobo.SmoboPhotoMetadata;

/**
 * Created by peter on 3/8/17.
 */

public class SmoboMediaAdapter extends BaseItemAdapter<SmoboMediaAdapter.ViewHolder, SmoboPhotoMetadata> {
    public SmoboMediaAdapter(List<SmoboPhotoMetadata> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.smobo_photo_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // we can assume that these integers will always be valid media id's
        holder.photo.setImageURI(MediaAPI.getMediaUri(items.get(position).id, MediaAPI.MediaSize.SMALL));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo)
        SimpleDraweeView photo;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
