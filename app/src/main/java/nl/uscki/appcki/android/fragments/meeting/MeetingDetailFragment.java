package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailFragment extends RefreshableFragment {
    MeetingItem item;

    TextView title;
    TextView plannedDate;
    TextView where;
    TextView mensen;
    TextView notes;
    TextView plannotes;
    TextView agenda;

    public MeetingDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_detail, container, false);

        setupSwipeContainer(view);

        // Inflate the layout for this fragment
        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), MeetingItem.class);
        }

        findViews(view);
        setupViews();

        return view;
    }

    private void setupViews() {
        title.setText(item.getMeeting().getTitle());

        DateTime dateTime = new DateTime(item.getMeeting().getStartdate());
        plannedDate.setText(dateTime.toString("EEEE dd MMMM YYYY HH:mm"));

        if (item.getMeeting().getLocation() == null || item.getMeeting().getLocation().isEmpty()) {
            where.setVisibility(View.GONE);
        } else {
            where.setText(item.getMeeting().getLocation());
        }
        mensen.setText(getMensenString(item));

        if (item.getMeeting().getNotes() != null && !item.getMeeting().getNotes().isEmpty()) {
            notes.setText(item.getMeeting().getNotes().trim());
        }

        plannotes.setText(item.getMeeting().getPlannotes());

        agenda.setText(item.getMeeting().getAgenda());
    }

    private void findViews(View view) {
        title = (TextView) view.findViewById(R.id.meeting_detail_title);
        plannedDate = (TextView) view.findViewById(R.id.meeting_detail_time);
        where = (TextView) view.findViewById(R.id.meeting_detail_where);
        mensen = (TextView) view.findViewById(R.id.meeting_detail_mensen);
        plannotes = (TextView) view.findViewById(R.id.meeting_detail_plannotes);
        notes = (TextView) view.findViewById(R.id.meeting_detail_notes);
        agenda = (TextView) view.findViewById(R.id.meeting_detail_agenda);
    }

    private String getMensenString(MeetingItem meeting) {
        return String.format(Locale.getDefault(), "%d / %d ( %d %%)", meeting.getEnrolledPersons().size(), meeting.getParticipation().size(),
                (int)(((float)meeting.getEnrolledPersons().size()/(float)meeting.getParticipation().size()) * 100));
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().meetingService.get(item.getMeeting().getId()).enqueue(new Callback<MeetingItem>() {
            @Override
            public void onSucces(Response<MeetingItem> response) {
                item = response.body();
                getView().invalidate();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
