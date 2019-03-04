package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingParticipantAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Participation;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingParticipantsFragment extends RefreshableFragment {
    MeetingItem item;
    boolean aanwezig;

    public MeetingParticipantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new MeetingParticipantAdapter(new ArrayList<PersonWithNote>()));
        MeetingActivity meetingActivity = (MeetingActivity) getActivity();
        if(getArguments() != null) {
            this.aanwezig = getArguments().getBoolean("aanwezig");
        }

        if(meetingActivity != null && meetingActivity.getMeetingItem() != null) {
            updateView(meetingActivity.getMeetingItem());
        }
    }

    private void updateView(MeetingItem item) {
        // Test if present

        if (getAdapter() instanceof MeetingParticipantAdapter) {
            if (aanwezig) {
                getAdapter().update(findAttendingPersons(item));
            } else {
                getAdapter().update(findNonAttendingPersons(item));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private List<PersonWithNote> findAttendingPersons(MeetingItem item) {
        List<PersonWithNote> personWithNotes = new ArrayList<>();
        if(item.getMeeting().getStartdate() != null) {
            for(Preference p : item.getMeeting().getActual_slot().getPreferences()) {
                personWithNotes.add(new PersonWithNote(p.getPerson(), p.getNotes()));
            }
        } else {
            for (PersonSimpleName person : item.getEnrolledPersons()) {
                personWithNotes.add(new PersonWithNote(person, ""));
            }
        }

        Log.d("MeetingParticipants", personWithNotes.size() + "");

        return personWithNotes;
    }

    private List<PersonWithNote> findNonAttendingPersons(MeetingItem item) {
        List<PersonWithNote> personWithNotes = new ArrayList<>();

        for(Participation p : item.getParticipation()) {
            if(item.getMeeting().getStartdate() != null) {
                for(Preference pref : item.getMeeting().getActual_slot().getPreferences()) {
                    if(pref.getPerson().equals(p.getPerson())) {

                    }
                }
            } else {
                //noinspection SuspiciousMethodCalls
                if(!item.getEnrolledPersons().contains(p.getPerson())) {
                    personWithNotes.add(new PersonWithNote(p.getPerson(), ""));
                }
            }
        }

        return personWithNotes;
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
        updateView(event.getUpdatedItem());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
