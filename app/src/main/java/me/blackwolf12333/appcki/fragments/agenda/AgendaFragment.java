package me.blackwolf12333.appcki.fragments.agenda;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.api.AgendaAPI;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.fragments.ProgressActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgendaFragment extends APIFragment {

    User user;

    private RecyclerView recyclerView;
    private ProgressActivity activity;

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
        AgendaAPI agendaAPI = new AgendaAPI(MainActivity.user);
        agendaAPI.getAgenda();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgressActivity)) {
            throw new IllegalArgumentException("This fragments acitivity must implement interface ProgressActivity");
        }
        this.activity = (ProgressActivity) activity;
    }

    public void onEventMainThread(AgendaEvent event) {
        activity.showProgress(false);
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

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
