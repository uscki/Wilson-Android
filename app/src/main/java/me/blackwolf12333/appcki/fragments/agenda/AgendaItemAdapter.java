package me.blackwolf12333.appcki.fragments.agenda;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.AgendaItem;

/**
 * Created by peter on 1/25/16.
 */
public class AgendaItemAdapter extends RecyclerView.Adapter<AgendaItemAdapter.ViewHolder> {
    private final List<AgendaItem> mValues;
    private Resources res;

    public AgendaItemAdapter(List<AgendaItem> items, Resources res) {
        Collections.reverse(items);
        mValues = items;
        this.res = res;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_agendaitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getWhat());
        holder.itemWhen.setText(mValues.get(position).getWhen());
        holder.itemDeelnemers.setText(mValues.get(position).getParticipants().size() + "");
        holder.itemWhere.setText(mValues.get(position).getWhere());

        //TODO set holder.itemPoster to an actual picture from AgendaItem.mediaCollection
        holder.itemPoster.setImageDrawable(res.getDrawable(R.drawable.poster));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO stuur een event naar de EventBus
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView itemWhen;
        public final TextView itemWhere;
        public final TextView itemDeelnemers;
        public final ImageView itemPoster;
        public AgendaItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.agendaitem_title);
            itemWhen = (TextView) view.findViewById(R.id.agendaitem_when);
            itemWhere = (TextView) view.findViewById(R.id.agendaitem_waar);
            itemDeelnemers = (TextView) view.findViewById(R.id.agendaitem_deelnemers);
            itemPoster = (ImageView) view.findViewById(R.id.agendaitem_poster);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}