package me.blackwolf12333.appcki.fragments.meeting.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.fragments.adapters.BaseItemAdapter;
import me.blackwolf12333.appcki.generated.organisation.PersonWithNote;
import me.blackwolf12333.appcki.views.NetworkImageView;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        holder.name.setText(items.get(position).getPerson());

        holder.note.setText(items.get(position).getNote());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/29/16 bekijk persoon
                Log.d("MeetingParticipantAdapt", "TODO: bekijk persoon");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Integer getLastID() {
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NetworkImageView profile;
        public final TextView name;
        public final TextView note;
        public PersonWithNote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            profile = (NetworkImageView) view.findViewById(R.id.person_list_item_profile);
            profile.setDefaultImageResId(R.drawable.account);

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
