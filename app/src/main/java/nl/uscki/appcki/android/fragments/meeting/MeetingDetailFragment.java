package nl.uscki.appcki.android.fragments.meeting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailFragment extends RefreshableFragment {
    private MeetingItem item;

    private TextView title;
    private TextView plannedDate;
    private TextView where;
    private TextView mensen;
    private TextView notes;
    private TextView plannotes;
    private TextView agenda;

    private CardView notesCard;
    private CardView planNotesCard;

    public MeetingDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_detail, container, false);
        this.title = view.findViewById(R.id.meeting_detail_title);
        this.plannedDate = view.findViewById(R.id.meeting_detail_time);
        this.where = view.findViewById(R.id.meeting_detail_where);
        this.mensen = view.findViewById(R.id.meeting_detail_mensen);
        this.notes = view.findViewById(R.id.meeting_detail_notes);
        this.plannotes = view.findViewById(R.id.meeting_detail_plannotes);
        this.agenda = view.findViewById(R.id.meeting_detail_agenda);
        this.notesCard = view.findViewById(R.id.meeting_detail_notes_card);
        this.planNotesCard = view.findViewById(R.id.meeting_detail_plannotes_card);

        setupSwipeContainer(view);

        MeetingActivity activity = (MeetingActivity) getActivity();
        if(activity != null && activity.getMeetingItem() != null) {
            this.item = activity.getMeetingItem();
            setupViews();
        }

        return view;
    }

    private void setupViews() {
        if(this.item == null) return;

        title.setText(item.getMeeting().getTitle());

        DateTime dateTime = new DateTime(item.getMeeting().getStartdate());
        plannedDate.setText(dateTime.toString("EEEE dd MMMM YYYY HH:mm"));

        if (item.getMeeting().getLocation() == null || item.getMeeting().getLocation().isEmpty()) {
            where.setVisibility(View.GONE);
        } else {
            where.setText(item.getMeeting().getLocation());
        }
        mensen.setText(getMensenString(item));
        this.mensen.setCompoundDrawablesWithIntrinsicBounds(item.getResponseStatus().getResponseStatusPeopleIcon(), 0, 0, 0);

        if (item.getMeeting().getNotes() != null && !item.getMeeting().getNotes().isEmpty()) {
            notes.setText(item.getMeeting().getNotes().trim());
            this.notesCard.setVisibility(View.VISIBLE);
        } else {
            this.notesCard.setVisibility(View.GONE);
        }

        if(item.getMeeting().getPlannotes() != null && !item.getMeeting().getPlannotes().isEmpty()) {
            plannotes.setText(item.getMeeting().getPlannotes());
            this.planNotesCard.setVisibility(View.VISIBLE);
        } else {
            this.planNotesCard.setVisibility(View.GONE);
        }

        agenda.setText(item.getMeeting().getAgenda());
    }

    private String getMensenString(MeetingItem meeting) {
        return String.format(Locale.getDefault(), "%d / %d ( %d %%)", meeting.getEnrolledPersons().size(), meeting.getParticipation().size(),
                (int)(((float)meeting.getEnrolledPersons().size()/(float)meeting.getParticipation().size()) * 100));
    }

    @Override
    public void onSwipeRefresh() {
        MeetingActivity activity = (MeetingActivity) getActivity();
        if(activity != null) {
            activity.refreshMeetingItem();
        } else {
            Log.e(getClass().getSimpleName(), "Activity refresh could not be performed!");
            swipeContainer.setRefreshing(false);
        }
    }

    public void onEventMainThread(DetailItemUpdatedEvent<MeetingItem> event) {
        swipeContainer.setRefreshing(false);
        this.item = event.getUpdatedItem();
        if(getView() != null) getView().invalidate();
        setupViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
