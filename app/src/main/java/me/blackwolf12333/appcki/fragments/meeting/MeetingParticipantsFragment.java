package me.blackwolf12333.appcki.fragments.meeting;


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

import me.blackwolf12333.appcki.api.Services;
import me.blackwolf12333.appcki.error.ConnectionError;
import me.blackwolf12333.appcki.fragments.PageableFragment;
import me.blackwolf12333.appcki.fragments.meeting.adapter.MeetingParticipantAdapter;
import me.blackwolf12333.appcki.generated.meeting.EnrolledPerson;
import me.blackwolf12333.appcki.generated.meeting.MeetingItem;
import me.blackwolf12333.appcki.generated.meeting.Preference;
import me.blackwolf12333.appcki.generated.meeting.Slot;
import me.blackwolf12333.appcki.generated.organisation.PersonWithNote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingParticipantsFragment extends PageableFragment {
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
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeContainer.setRefreshing(false);
        return view;
    }

    private List<PersonWithNote> findAttendingPersons(MeetingItem item) {
        List<PersonWithNote> personWithNotes = new ArrayList<>();

        for (PersonSimple person : item.getEnrolledPersons()) {
            personWithNotes.add(new PersonWithNote(person.getPostalname(), ""));
        }

        Log.d("MeetingParticipants", personWithNotes.size() + "");

        return personWithNotes;
    }

    private List<PersonWithNote> findNonAttendingPersons(MeetingItem item) {
        List<PersonWithNote> personWithNotes = new ArrayList<>();

        for (Slot slot : item.getSlots()) {
            for (Preference pref : slot.getPreferences()) {
                if (!personWithNotes.contains(new PersonWithNote(pref.getPerson().getName(), ""))) {
                    if (!pref.getCanattend()) {
                        personWithNotes.add(new PersonWithNote(pref.getPerson().getName(), pref.getNotes()));
                    }
                }
            }
        }

        Log.d("findNonAttending", "found " + personWithNotes.size());

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

    @Override
    public void onScrollRefresh() {

    }
}
