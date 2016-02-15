package me.blackwolf12333.appcki.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyAgenda;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends APIFragment {
    private RecyclerView recyclerView;
    VolleyAgenda agendaAPI = new VolleyAgenda();

    public AgendaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agendaAPI.getAgendaNewer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agendaitem_list, container, false);

        if(view instanceof RecyclerView) {
            this.recyclerView = (RecyclerView) view;
        }
        return view;
    }

    @Override
    public void refresh() {
        agendaAPI.getAgendaNewer();
    }

    public void onEventMainThread(AgendaEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        recyclerView.setAdapter(new AgendaItemAdapter(event.agenda.getContent()));
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
