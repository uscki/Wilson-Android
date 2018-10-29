package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.shop.Store;

public class StoreAdapter extends BaseItemAdapter<StoreAdapter.ViewHolder, Store> {
    public StoreAdapter(List<Store> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        final Store store = items.get(position);

        holder.name.setText(store.title);
        holder.image.setImageURI("");

        if (store.image != null)
            holder.image.setImageURI(MediaAPI.getMediaUri(store.image));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", store.id);
                EventBus.getDefault().post(new OpenFragmentEvent(new StoreFragment(), bundle));
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.store_name)
        TextView name;
        @BindView(R.id.store_image)
        SimpleDraweeView image;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
