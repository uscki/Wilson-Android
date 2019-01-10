package nl.uscki.appcki.android.fragments.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.agenda_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        unsetViews(holder);
        resetViews(holder, items.get(position));
    }

    private void resetViews(final ViewHolder holder, AgendaItem item) {
        holder.mItem = item;
        holder.mContentView.setText(item.getTitle());

        if (item.getEnd() != null) {
            String when = item.getStart().toString("EEEE dd MMMM YYYY HH:mm") + " - " + item.getEnd().toString("EEEE dd MMMM YYYY HH:mm");
            holder.itemWhen.setText(when);
        } else {
            holder.itemWhen.setText(item.getStart().toString("EEEE dd MMMM YYYY HH:mm"));
        }

        Context c = holder.mView.getContext();
        String nRegistrationsString;
        if(item.getMaxregistrations() == null) {
            nRegistrationsString = c.getString(R.string.agenda_item_n_registrations, item.getParticipants().size());
        } else if(item.getMaxregistrations().equals(0)) {
            nRegistrationsString = c.getString(R.string.agenda_prepublished_event_registration_closed_short_message);
        } else if(item.getBackupList().size() > 0) {
            nRegistrationsString = c.getString(
                    R.string.agenda_item_n_registration_plus_backup,
                    item.getParticipants().size(),
                    item.getBackupList().size(),
                    item.getMaxregistrations());
        } else {
            nRegistrationsString = c.getString(
                    R.string.agenda_item_n_registrations_max,
                    item.getParticipants().size(),
                    item.getMaxregistrations()
            );
        }
        holder.itemDeelnemers.setText(nRegistrationsString);

        if(item.getLocation() == null || item.getLocation().isEmpty()) {
            holder.itemWhere.setVisibility(View.GONE);
        } else {
            holder.itemWhere.setText(item.getLocation());
        }

        if(item.getHasDeadline()) {
            DateTime dateTime = new DateTime(item.getDeadline());
            holder.itemDeadline.setText(dateTime.toString("EEEE dd MMMM YYYY HH:mm")); // TODO API: richard gaat hier nog shit aan veranderen
        } else {
            holder.inschrijvenVerplicht.setVisibility(View.GONE);
        }

        if (item.getPosterid() != null) {
            holder.itemPoster.setImageURI(MediaAPI.getMediaUri(item.getPosterid(), MediaAPI.MediaSize.SMALL));
            //TODO open media browser with normal size poster
        }

        if(item.getMaxregistrations() != null && item.getMaxregistrations() == 0) {
            holder.prepublishedNotice.setVisibility(View.VISIBLE);
            holder.inschrijvenVerplicht.setVisibility(View.VISIBLE);
            holder.itemDeadline.setVisibility(View.GONE);
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

    private void unsetViews(ViewHolder holder) {
        holder.mItem = null;
        holder.itemPoster.setImageURI("");
        holder.itemWhere.setText("");
        holder.itemDeadline.setText("");
        holder.itemDeelnemers.setText("");
        holder.itemWhen.setText("");
        holder.mContentView.setText("");

        holder.inschrijvenVerplicht.setVisibility(View.VISIBLE);
        holder.itemWhere.setVisibility(View.VISIBLE);
        holder.prepublishedNotice.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView itemWhen;
        public final TextView itemWhere;
        public final TextView itemDeelnemers;
        public final SimpleDraweeView itemPoster;
        public final TextView itemDeadline;
        public final View inschrijvenVerplicht;
        public final TextView prepublishedNotice;
        public AgendaItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.agenda_item_title);
            itemWhen = (TextView) view.findViewById(R.id.agenda_item_when);
            itemWhere = (TextView) view.findViewById(R.id.agenda_item_waar);
            itemDeelnemers = (TextView) view.findViewById(R.id.agenda_item_deelnemers);
            itemPoster = (SimpleDraweeView) view.findViewById(R.id.agenda_item_poster);
            itemDeadline = (TextView) view.findViewById(R.id.inschrijven_verplicht_date);
            inschrijvenVerplicht = view.findViewById(R.id.agenda_inschrijven_verplicht);
            prepublishedNotice = view.findViewById(R.id.prepublished_event_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
