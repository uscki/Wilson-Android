package nl.uscki.appcki.android.fragments.meeting.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;

/**
 * Created by peter on 7/30/16.
 */
public class MeetingParticipantAdapter extends BaseItemAdapter<MeetingParticipantAdapter.ViewHolder, PersonWithNote> {
    public MeetingParticipantAdapter(List<PersonWithNote> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_person_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        holder.name.setText(items.get(position).getPerson());

        holder.note.setText(items.get(position).getNote());

        if(items.get(position).getPhotoid() != null) {
            Services.getInstance().picasso
                    .load(MediaAPI.getMediaUrl(items.get(position).getPhotoid(), MediaAPI.MediaSize.SMALL))
                    .placeholder(R.drawable.account)
                    .into(holder.profile);
        }

        final Rect startBounds = new Rect();
        holder.profile.getGlobalVisibleRect(startBounds);

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EventBus.getDefault().post(new ImageZoomEvent(startBounds, holder.profile.getMediaId()));
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/29/16 bekijk persoon
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView profile;
        public final TextView name;
        public final TextView note;
        public PersonWithNote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (ImageView) view.findViewById(R.id.person_list_item_profile);

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
