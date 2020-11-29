package nl.uscki.appcki.android.fragments.media.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.FullScreenMediaActivity;
import nl.uscki.appcki.android.generated.media.MediaTag;

public class ImageTagAdapter extends RecyclerView.Adapter<ImageTagAdapter.ViewHolder> {

    private FullScreenMediaActivity activity;
    private List<MediaTag> tags;

    public ImageTagAdapter(FullScreenMediaActivity mediaActivity, List<MediaTag> tags) {
        this.tags = tags;
        this.activity = mediaActivity;
    }

    @NonNull
    @Override
    public ImageTagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.image_tag, parent, false);
        return new ImageTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageTagAdapter.ViewHolder holder, int position) {
        holder.username.setText(tags.get(position).getName());
        holder.itemView.setOnLongClickListener(v -> false);
        holder.itemView.setOnClickListener(v -> Log.v("MediaTag", "Tag clicked: Implement media view for tags! " + tags.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.image_tag_username);
        }
    }
}