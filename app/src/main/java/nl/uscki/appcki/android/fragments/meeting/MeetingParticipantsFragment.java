package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingParticipantAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Participation;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.generated.organisation.PersonName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import retrofit2.Response;

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
        if (getArguments() != null) {
            Gson gson = new Gson();
            item = gson.fromJson(getArguments().getString("item"), MeetingItem.class);
            aanwezig = getArguments().getBoolean("aanwezig");

            if (aanwezig) {
                setAdapter(new MeetingParticipantAdapter(findAttendingPersons(item)));
                Log.d("MeetingParticipants", ""+aanwezig);
            } else {
                setAdapter(new MeetingParticipantAdapter(findNonAttendingPersons(item)));
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
        Services.getInstance().meetingService.get(item.getMeeting().getId()).enqueue(new Callback<MeetingItem>() {
            @Override
            public void onSucces(Response<MeetingItem> response) {
                swipeContainer.setRefreshing(false);
                if (getAdapter() instanceof MeetingParticipantAdapter) {
                    MeetingItem item = response.body();
                    if (aanwezig) {
                        getAdapter().update(findAttendingPersons(item));
                    } else {
                        getAdapter().update(findNonAttendingPersons(item));
                    }
                }
            }
        });
    }
}
