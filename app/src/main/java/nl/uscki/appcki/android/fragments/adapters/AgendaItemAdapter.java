package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.views.NetworkImageView;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;

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
        holder.mContentView.setText(item.getTitle());

        if (item.getEnd() != null) {
            String when = item.getStart().toString("EEEE dd MMMM YYYY HH:mm") + " - " + item.getEnd().toString("EEEE dd MMMM YYYY HH:mm");
            holder.itemWhen.setText(when);
        } else {
            holder.itemWhen.setText(item.getStart().toString("EEEE dd MMMM YYYY HH:mm"));
        }

        holder.itemDeelnemers.setText(item.getParticipants().size() + "");
        holder.itemWhere.setText(item.getLocation());

        if(item.getHasDeadline()) {
            DateTime dateTime = new DateTime(item.getDeadline());
            holder.itemDeadline.setText(dateTime.toString("EEEE dd MMMM YYYY HH:mm")); // TODO API: richard gaat hier nog shit aan veranderen
            holder.inschrijvenVerplicht.setVisibility(View.VISIBLE);
        } else {
            holder.inschrijvenVerplicht.setVisibility(View.GONE);
        }

        //holder.itemPoster.setDefaultImageResId(R.drawable.default_poster);
        if(item.getPosterid() != null) {
            holder.itemPoster.setVisibility(View.VISIBLE); // als het vorige item in deze holder invisible was moet het weer visible worden
            holder.itemPoster.setImageMediaId(item.getPosterid());
        } else {
            holder.itemPoster.setVisibility(View.INVISIBLE);
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
        public final TextView itemDeadline;
        public final View inschrijvenVerplicht;
        public AgendaItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.agenda_item_title);
            itemWhen = (TextView) view.findViewById(R.id.agenda_item_when);
            itemWhere = (TextView) view.findViewById(R.id.agenda_item_waar);
            itemDeelnemers = (TextView) view.findViewById(R.id.agenda_item_deelnemers);
            itemPoster = (NetworkImageView) view.findViewById(R.id.agenda_item_poster);
            itemDeadline = (TextView) view.findViewById(R.id.inschrijven_verplicht_date);
            inschrijvenVerplicht = view.findViewById(R.id.agenda_inschrijven_verplicht);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
