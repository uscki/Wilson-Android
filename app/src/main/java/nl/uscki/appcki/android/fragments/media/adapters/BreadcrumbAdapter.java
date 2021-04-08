package nl.uscki.appcki.android.fragments.media.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.media.MediaCollection;

public class BreadcrumbAdapter extends RecyclerView.Adapter<BreadcrumbAdapter.ViewHolder> {

    private List<MediaCollection> mediaCollectionList;

    public BreadcrumbAdapter(List<MediaCollection> mediaCollectionList) {
        this.mediaCollectionList = mediaCollectionList;
    }

    public void addItems(List<MediaCollection> mediaCollections) {
        this.mediaCollectionList.addAll(mediaCollections);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.breadcrumb_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.crumbImage.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        if(position == getItemCount() - 1) {
            holder.crumbTitle.setTypeface(null, Typeface.BOLD);
        }
        holder.crumbTitle.setText(this.mediaCollectionList.get(position).name);
        holder.itemView.setOnClickListener(v -> {
            // TODO find a way to do this, preferably just triggering onBackPressed a few times
            Log.e("MediaBreadcrumbs", "Clicked to go back to position " + position);
        });
    }

    @Override
    public int getItemCount() {
        return this.mediaCollectionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView crumbImage;
        private TextView crumbTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.crumbImage = itemView.findViewById(R.id.crumb_image);
            this.crumbTitle = itemView.findViewById(R.id.crumb_title);
        }
    }
}
