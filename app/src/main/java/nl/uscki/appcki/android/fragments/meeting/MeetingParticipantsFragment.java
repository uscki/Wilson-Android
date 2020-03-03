package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
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
    boolean aanwezig;
    private TextView emptyText;
    private NestedScrollView emptyTextScrollView;

    public MeetingParticipantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new MeetingParticipantAdapter(new ArrayList<PersonWithNote>()));
        if(getArguments() != null) {
            this.aanwezig = getArguments().getBoolean("aanwezig");
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

        if(getAdapter().getItemCount() == 0) {
            this.emptyTextScrollView.setVisibility(View.VISIBLE);
            this.emptyText.setText(getEmptyText(item));
        } else {
            this.emptyTextScrollView.setVisibility(View.GONE);
        }
    }

    private int getEmptyText(MeetingItem item) {
        if(item.getMeeting().getStartdate() == null) {
            // Not yet planned
            return this.aanwezig ? R.string.meeting_participant_empty_responded : R.string.meeting_participant_empty_not_responded;
        } else {
            return this.aanwezig ? R.string.meeting_participant_empty_available : R.string.meeting_participant_empty_unavailable;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.emptyTextScrollView = view.findViewById(R.id.empty_text_scrollview);
        this.emptyText = view.findViewById(R.id.empty_text);
        MeetingActivity meetingActivity = (MeetingActivity) getActivity();
        if(meetingActivity != null && meetingActivity.getMeetingItem() != null) {
            updateView(meetingActivity.getMeetingItem());
        }
        return view;
    }

    private List<PersonWithNote> findAttendingPersons(MeetingItem item) {
        List<PersonWithNote> personWithNotes = new ArrayList<>();
        if(item.getMeeting().getStartdate() != null) {
            // Meeting is planned
            for(Preference p : item.getMeeting().getActual_slot().getPreferences()) {
                if(p.getCanattend()) {
                    personWithNotes.add(new PersonWithNote(p.getPerson(), p.getNotes()));
                }
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
                notResponded.add(new PersonWithNote(p.getPerson(), ""));
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
        updateView(event.getUpdatedItem());
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
