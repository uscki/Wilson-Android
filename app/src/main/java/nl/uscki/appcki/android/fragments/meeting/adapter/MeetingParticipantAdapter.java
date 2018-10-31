package nl.uscki.appcki.android.fragments.meeting.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.activities.SmoboActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import retrofit2.Response;

/**
 * Created by peter on 7/30/16.
 */
public class MeetingParticipantAdapter extends BaseItemAdapter<MeetingParticipantAdapter.ViewHolder, PersonWithNote> {
    public MeetingParticipantAdapter(List<PersonWithNote> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, final int position) {
        unsetViews(holder);
        resetViews(holder, items.get(position));
    }

    private void resetViews(final ViewHolder holder, PersonWithNote item) {
        holder.mItem = item;
        holder.name.setText(item.getPerson().getPostalname());

        holder.note.setText(item.getNote());

        if(item.getPerson().getPhotomediaid() != null && item.getPerson().getPhotomediaid() != 0) {
            holder.profile.setImageURI(MediaAPI.getMediaUri(item.getPerson().getPhotomediaid(), MediaAPI.MediaSize.SMALL));
        }

        final MeetingActivity meetingActivity = (MeetingActivity)holder.mView.getContext();
        if(meetingActivity != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    meetingActivity.openSmoboFor(holder.mItem.getPerson());
                }
            });
        }
    }

    private void unsetViews(ViewHolder vh) {
        vh.mItem = null;
        vh.name.setText("");
        vh.note.setText("");
        vh.profile.setImageURI("");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView profile;
        public final TextView name;
        public final TextView note;
        public PersonWithNote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (SimpleDraweeView) view.findViewById(R.id.person_list_item_profile);

            name = (TextView) view.findViewById(R.id.person_list_item_name);
            note = (TextView) view.findViewById(R.id.person_list_item_note);
            note.setVisibility(View.VISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
