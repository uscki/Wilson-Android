package me.blackwolf12333.appcki.fragments.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.MediaAPI;
import me.blackwolf12333.appcki.api.common.APISingleton;
import me.blackwolf12333.appcki.api.media.ImageLoader;
import me.blackwolf12333.appcki.api.media.NetworkImageView;
import me.blackwolf12333.appcki.events.OpenFragmentEvent;
import me.blackwolf12333.appcki.fragments.agenda.AgendaDetailTabsFragment;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 *
 */
public class AgendaItemAdapter extends BaseItemAdapter<AgendaItemAdapter.ViewHolder, AgendaItem> {

    public AgendaItemAdapter(List<AgendaItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.agenda_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AgendaItem item = items.get(position);
        holder.mItem = item;
        holder.mContentView.setText(item.getWhat());
        holder.itemWhen.setText(item.getWhen());
        holder.itemDeelnemers.setText(item.getParticipants().size() + "");
        holder.itemWhere.setText(item.getWhere());

        holder.itemPoster.setDefaultImageResId(R.drawable.default_poster);
        if(item.getPosterid() != null) {
            ImageLoader loader = APISingleton.getInstance(App.getContext()).getImageLoader();
            holder.itemPoster.setImageIdAndType(item.getPosterid().getId(), MediaAPI.getFiletypeFromMime(item.getPosterid().getMimetype()), loader);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Gson gson = new Gson();
                String json = gson.toJson(holder.mItem, AgendaItem.class);
                args.putString("item", json);
                EventBus.getDefault().post(new OpenFragmentEvent(new AgendaDetailTabsFragment(), args));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Integer getLastID() {
        return items.get(items.size()-1).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView itemWhen;
        public final TextView itemWhere;
        public final TextView itemDeelnemers;
        public final NetworkImageView itemPoster;
        public AgendaItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.agenda_item_title);
            itemWhen = (TextView) view.findViewById(R.id.agenda_item_when);
            itemWhere = (TextView) view.findViewById(R.id.agenda_item_waar);
            itemDeelnemers = (TextView) view.findViewById(R.id.agenda_item_deelnemers);
            itemPoster = (NetworkImageView) view.findViewById(R.id.agenda_item_poster);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
