package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.UserHelper;

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
        holder.title.setText(item.getMeeting().getTitle());

        if (item.getMeeting().getStartdate() != null) {
            DateTime dateTime = new DateTime(item.getMeeting().getStartdate());
            holder.plannedDate.setText(dateTime.toString("EEEE dd MMMM YYYY HH:mm"));
        } else {
            holder.plannedDate.setVisibility(View.GONE);
        }

        if (item.getMeeting().getLocation() == null || item.getMeeting().getLocation().isEmpty()) {
            holder.where.setVisibility(View.GONE);
        } else {
            holder.where.setText(item.getMeeting().getLocation());
        }
        holder.mensen.setText(getMensenString(item));
        holder.status.setText(getStatusString(item));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("id", holder.mItem.getMeeting().getId());
                // TODO: 7/3/16 launch vergaderplanner fragment
                EventBus.getDefault().post(new OpenFragmentEvent(new MeetingDetailTabsFragment(), args));
            }
        });
    }

    private String getStatusString(MeetingItem meeting) {
        if (meeting.getMeeting().getStartdate() != null) {
            return "Deze vergadering is al gepland";
        } else {
            //noinspection SuspiciousMethodCalls
            if (!meeting.getEnrolledPersons().contains(UserHelper.getInstance().getPerson())) {
                return "Je hebt nog niet gereageerd.";
            } else {
                return "Deze vergadering is nog niet gepland";
            }
        }
    }

    private String getMensenString(MeetingItem meeting) {
        return String.format(Locale.getDefault(), "%d / %d ( %d %%)", meeting.getEnrolledPersons().size(), meeting.getParticipation().size(),
                (int)(((float)meeting.getEnrolledPersons().size()/(float)meeting.getParticipation().size()) * 100));
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
        public final TextView where;
        public final TextView mensen;
        public final TextView status;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.meeting_overview_title);
            plannedDate = (TextView) view.findViewById(R.id.meeting_overview_time_text);
            where = (TextView) view.findViewById(R.id.meeting_overview_where_text);
            mensen = (TextView) view.findViewById(R.id.meeting_overview_mensen_text);
            status = (TextView) view.findViewById(R.id.meeting_overview_status_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '"  + "'";
        }
    }
}
