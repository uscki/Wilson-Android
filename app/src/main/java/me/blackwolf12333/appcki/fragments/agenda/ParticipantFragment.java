package me.blackwolf12333.appcki.fragments.agenda;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyAgenda;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.Participant;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class ParticipantFragment extends APIFragment {
    List<Participant> participants;
    VolleyAgenda agendaAPI = new VolleyAgenda();
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParticipantFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        agendaAPI.getAgendaItem(getArguments().getInt("id"));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participant_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
        }
        return view;
    }

    public void onEventMainThread(AgendaItemEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        participants = event.agendaItem.getParticipants();
        recyclerView.setAdapter(new ParticipantRecyclerViewAdapter(participants));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
