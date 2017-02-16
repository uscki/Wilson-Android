package nl.uscki.appcki.android.fragments.meeting.adapter;


import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingPreferenceDayAdapter extends RecyclerView.Adapter<MeetingPreferenceDayAdapter.ViewHolder> {
    List<DaySlots> slots;

    public MeetingPreferenceDayAdapter(List<DaySlots> slots) {
        this.slots = slots;
    }

    @Override
    public MeetingPreferenceDayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_meeting_preference_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MeetingPreferenceDayAdapter.ViewHolder holder, int position) {
        DaySlots daySlots = slots.get(position);
        holder.daySlots = daySlots;
        holder.date.setText(daySlots.getDay());
        holder.slots.setAdapter(new MeetingPreferenceDaySlotAdapter(daySlots.getSlots()));
        holder.collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.daySlots.isCollapsed) {
                    holder.slots.setVisibility(View.VISIBLE);
                    holder.daySlots.isCollapsed = false;
                    holder.collapse.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.minus));
                } else {
                    holder.slots.setVisibility(View.GONE);
                    holder.daySlots.isCollapsed = true;
                    holder.collapse.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.plus_dark));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public DaySlots daySlots;
        public final View mView;
        public final TextView date;
        public final RecyclerView slots;
        public final ImageView collapse;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.date = (TextView) view.findViewById(R.id.meeting_preference_day_text);
            this.slots = (RecyclerView) view.findViewById(R.id.list);
            this.collapse = (ImageView) view.findViewById(R.id.meeting_preference_collapse);
        }
    }
}
