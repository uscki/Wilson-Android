package me.blackwolf12333.appcki.fragments.agenda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.AgendaAPI;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgendaFragment extends APIFragment {
    private RecyclerView recyclerView;
    private AgendaAPI agendaAPI = new AgendaAPI();

    public AgendaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AgendaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgendaFragment newInstance() {
        AgendaFragment fragment = new AgendaFragment();
        return fragment;
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

    public void onEventMainThread(AgendaEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        recyclerView.setAdapter(new AgendaItemAdapter(event.agenda.getContent(), this.getResources()));
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
