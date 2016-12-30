package nl.uscki.appcki.android.fragments.meeting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.fragments.meeting.adapter.MeetingParticipantAdapter;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Participation;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.organisation.PersonWithNote;
import retrofit2.Call;
import retrofit2.Callback;
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
                personWithNotes.add(new PersonWithNote(p.getPerson().getPostalname(), p.getNotes(), p.getPerson().getPhotomediaid()));
            }
        } else {
            for (PersonSimpleName person : item.getEnrolledPersons()) {
                personWithNotes.add(new PersonWithNote(person.getPostalname(), "", person.getPhotomediaid()));
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
                    personWithNotes.add(new PersonWithNote(p.getPerson().getPostalname(), "", p.getPerson().getPhotomediaid()));
                }
            }
        }

        return personWithNotes;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().meetingService.get(item.getMeeting().getId()).enqueue(new Callback<MeetingItem>() {
            @Override
            public void onResponse(Call<MeetingItem> call, Response<MeetingItem> response) {
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

            @Override
            public void onFailure(Call<MeetingItem> call, Throwable t) {
                if (t instanceof ConnectException) {
                    new ConnectionError(t); // handle connection error in MainActivity
                } else {
                    throw new RuntimeException(t);
                }
            }
        });
    }
}
