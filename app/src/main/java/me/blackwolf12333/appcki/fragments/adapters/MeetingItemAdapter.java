package me.blackwolf12333.appcki.fragments.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.meeting.MeetingItem;

/**
 * Created by peter on 7/3/16.
 */
public class MeetingItemAdapter extends BaseItemAdapter<MeetingItemAdapter.ViewHolder, MeetingItem> {
    public MeetingItemAdapter(List<MeetingItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_meeting_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MeetingItem item = items.get(position);
        holder.mItem = item;
        Log.d("MeetingItemAdapter", item.toString());
        holder.title.setText(item.getMeeting().getTitle());
        holder.plannedDate.setText(item.getMeeting().getActualTime()); //TODO API: Richard fixt dit
        holder.where.setText(item.getMeeting().getLocation());
        holder.mensen.setText(getMensenString(item));
        holder.status.setText(getStatusString(item));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Gson gson = new Gson();
                String json = gson.toJson(holder.mItem, MeetingItem.class);
                args.putString("item", json);
                // TODO: 7/3/16 launch vergaderplanner fragment
                //EventBus.getDefault().post(new OpenFragmentEvent(new AgendaDetailTabsFragment(), args));
            }
        });
    }

    private String getStatusString(MeetingItem meeting) {
        if (meeting.getMyPreferences().isEmpty()) {
            return "Je hebt nog niet gereageerd.";
        } else {
            if (meeting.getMeeting().getActualTime().isEmpty()) {
                return "Deze vergadering is nog niet gepland";
            } else {
                return "Deze vergadering is al gepland";
            }
        }
    }

    private String getMensenString(MeetingItem meeting) {
        Log.d("MeetingItemAdapter", ""+meeting.getSlots().size());
        String mensen = "blabla";//String.format("%d / %d (%d %)", meeting.getParticipants().size());
        return mensen;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Integer getLastID() {
        return items.get(items.size()-1).getMeeting().getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public MeetingItem mItem;
        public final TextView title;
        public final TextView plannedDate;
        public final ImageView plannedIcon;
        public final TextView where;
        public final TextView mensen;
        public final TextView status;
        public final ImageView statusIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.meeting_overview_title);
            plannedDate = (TextView) view.findViewById(R.id.meeting_overview_time_text);
            plannedIcon = (ImageView) view.findViewById(R.id.meeting_overview_time_icon);
            where = (TextView) view.findViewById(R.id.meeting_overview_where_text);
            mensen = (TextView) view.findViewById(R.id.meeting_overview_mensen_text);
            status = (TextView) view.findViewById(R.id.meeting_overview_status_text);
            statusIcon = (ImageView) view.findViewById(R.id.meeting_overview_status_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '"  + "'";
        }
    }
}
