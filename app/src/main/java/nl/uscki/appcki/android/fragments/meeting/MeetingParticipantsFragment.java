package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingParticipantAdapter;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.ListSectionHeader;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Participation;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.generated.organisation.PersonName;
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
            for (PersonName person : item.getEnrolledPersons()) {
                personWithNotes.add(new PersonWithNote(person, ""));
            }
        }

        Log.d("MeetingParticipants", personWithNotes.size() + "");

        return personWithNotes;
    }

    private List<IWilsonBaseItem> findNonAttendingPersons(MeetingItem item) {
        List<IWilsonBaseItem> personWithNotes = new ArrayList<>();
        List<IWilsonBaseItem> notResponded = new ArrayList<>();

        for(Participation p : item.getParticipation()) {                    // all invited people
            if(item.getMeeting().getStartdate() == null) {                  // Not yet planned
                if(!item.getEnrolledPersons().contains(p.getPerson())) {    // filter non-responders
                    personWithNotes.add(new PersonWithNote(p.getPerson(), ""));
                }
            } else if (p.getPreferences().size() > 0) {                     // Meeting planned
                boolean preferenceFound = false;
                for(Preference pref : item.getMeeting().getActual_slot().getPreferences()) {
                    if(pref.getPerson().equals(p.getPerson()) && !pref.getCanattend()) {
                        personWithNotes.add(new PersonWithNote(pref.getPerson(), pref.getNotes()));
                        preferenceFound = true;
                        break;
                    } else if (pref.getCanattend()) {
                        preferenceFound = true;
                    }
                }
                if(!preferenceFound) {
                    // Assumption here is that if the participation exists, but not the preference, they didn't respond
                    notResponded.add(new PersonWithNote(p.getPerson(), ""));
                }
            } else {
                notResponded.add(new PersonWithNote(p.getPerson(), Integer.toString(p.getPreferences().size())));
            }
        }

        if(notResponded.size() > 0) {
            ListSectionHeader h = new ListSectionHeader(getResources().getString(R.string.meeting_header_no_response));
            personWithNotes.add(h);
            personWithNotes.addAll(notResponded);
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
