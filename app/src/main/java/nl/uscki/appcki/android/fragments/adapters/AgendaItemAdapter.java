package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.generated.agenda.SimpleAgendaItem;
import nl.uscki.appcki.android.helpers.AgendaSubscribedHelper;

/**
 *
 */
public class AgendaItemAdapter extends BaseItemAdapter<AgendaItemAdapter.ViewHolder, SimpleAgendaItem> {

    public AgendaItemAdapter(List<SimpleAgendaItem> items) {
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

    private void resetViews(final ViewHolder holder, SimpleAgendaItem item) {
        holder.mItem = item;
        holder.mContentView.setText(item.getTitle().replaceAll("CKI", App.USCKI_CKI_CHARACTER));

        String when = AgendaSubscribedHelper.getWhen(item);
        holder.itemWhen.setText(when);

        holder.itemDeelnemers.setText(AgendaSubscribedHelper.getParticipantsSummary(holder.mView.getContext(), item));

        // TODO view allows showing registration status, but that information is not available in the simple agenda item
        // TODO, below checks should use user subscribe status, not total count. Uncomment until available on model
/*        Drawable participantIcon = holder.mView.getContext().getResources().getDrawable(R.drawable.account_multiple);
        if(item.getTotalParticipants() > 0) {
            participantIcon = holder.mView.getContext().getResources().getDrawable(R.drawable.account_multiple_subscribed);
        } if (item.getTotalBackuplist() > 0) {
            participantIcon = holder.mView.getContext().getResources().getDrawable(R.drawable.account_multiple_backup);
        }
        holder.itemDeelnemers.setCompoundDrawablesRelativeWithIntrinsicBounds(participantIcon, null, null, null);
 */

        if(item.getLocation() == null || item.getLocation().isEmpty()) {
            holder.itemWhere.setVisibility(View.GONE);
        } else {
            holder.itemWhere.setText(item.getLocation());
        }

        if(item.getHasDeadline()) {
            holder.registrationCompulsoryText.setVisibility(View.VISIBLE);

            holder.registrationCompulsoryText.setText(
                    holder.mView.getResources().getString(
                            R.string.agenda_registration_required_before,
                            item.getDeadline().toString("EEEE dd MMMM YYYY HH:mm")));
        } else {
            holder.registrationCompulsoryText.setVisibility(View.GONE);
            holder.registrationCompulsoryText.setOnLongClickListener(null);
        }

        if((item.getHasDeadline() && item.getDeadline().isBeforeNow()) || (item.getStart().isBeforeNow() && !item.getHasDeadline())) {
            holder.registrationCompulsoryText.setVisibility(View.VISIBLE);
            holder.registrationCompulsoryText.setText(R.string.agenda_event_registration_closed);
        }

        if (item.getPosterid() != null) {
            holder.itemPoster.setImageURI(MediaAPI.getMediaUri(item.getPosterid(), MediaAPI.MediaSize.SMALL));
            //TODO open media browser with normal size poster
        }

        if(item.getMaxregistrations() != null && item.getMaxregistrations() == 0) {
            holder.itemDeelnemers.setText(R.string.agenda_prepublished_event_registration_opens_later_short_message);
        }

        if(item.getTotalComments() > 0) {
            holder.nComments.setText(holder.mView.getContext().getString(
                    item.getTotalComments() == 1 ? R.string.agenda_n_comments_singular : R.string.agenda_n_comments_plural,
                    item.getTotalComments()));
            holder.nComments.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt(AgendaActivity.PARAM_AGENDA_ID, holder.mItem.getId());
                EventBus.getDefault().post(new OpenFragmentEvent(new AgendaDetailTabsFragment(), args));
            }
        });
    }

    private void unsetViews(ViewHolder holder) {
        holder.mItem = null;
        holder.itemPoster.setImageURI("");
        holder.itemWhere.setText("");
        holder.itemDeelnemers.setText("");
        holder.itemWhen.setText("");
        holder.mContentView.setText("");

        holder.itemWhere.setVisibility(View.VISIBLE);
        holder.nComments.setText("");
        holder.nComments.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public SimpleAgendaItem mItem;

        TextView mContentView;
        TextView itemWhen;
        TextView itemWhere;
        TextView nComments;
        TextView itemDeelnemers;
        SimpleDraweeView itemPoster;
        TextView registrationCompulsoryText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            this.mContentView = view.findViewById(R.id.agenda_item_title);
            this.itemWhen = view.findViewById(R.id.agenda_item_when);
            this.itemWhere = view.findViewById(R.id.agenda_item_waar);
            this.nComments = view.findViewById(R.id.agenda_item_comment_number);
            this.itemDeelnemers = view.findViewById(R.id.agenda_item_deelnemers);
            this.itemPoster = view.findViewById(R.id.agenda_item_poster);
            this.registrationCompulsoryText = view.findViewById(R.id.registration_compulsory);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
