package me.blackwolf12333.appcki.fragments.agenda;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyAgenda;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.fragments.APIFragment;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * A simple {@link APIFragment} subclass.
 */
public class AgendaFragment extends APIFragment {
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    protected AgendaItemAdapter adapter = new AgendaItemAdapter(new ArrayList<AgendaItem>());
    VolleyAgenda agendaAPI = new VolleyAgenda();

    public AgendaFragment() {
        agendaAPI.getAgendaNewer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        setupSwipeContainer(view);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);
    }

    public void updateContent(List<AgendaItem> content) {
        adapter.clear();
        adapter.addAll(content);
        swipeContainer.setRefreshing(false);
    }

    public void onEventMainThread(AgendaEvent event) {
        EventBus.getDefault().post(new ShowProgressEvent(false));
        this.updateContent(event.agenda.getContent());
    }

    @Override
    public void refresh() {
        agendaAPI.getAgendaNewer();
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
