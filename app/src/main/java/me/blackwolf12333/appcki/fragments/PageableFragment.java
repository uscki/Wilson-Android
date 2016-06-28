package me.blackwolf12333.appcki.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyAgenda;
import me.blackwolf12333.appcki.api.VolleyNews;
import me.blackwolf12333.appcki.api.VolleyRoephoek;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.NewsOverviewEvent;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.fragments.adapters.AgendaItemAdapter;
import me.blackwolf12333.appcki.fragments.adapters.BaseItemAdapter;
import me.blackwolf12333.appcki.fragments.adapters.NewsItemAdapter;
import me.blackwolf12333.appcki.fragments.adapters.RoephoekItemAdapter;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.generated.roephoek.RoephoekItem;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class PageableFragment extends Fragment {

    private int type;
    private BaseItemAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    public static final int NEWS = 0;
    public static final int AGENDA = 1;
    public static final int ROEPHOEK = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PageableFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        Log.d("PageableFragment", "type=" + type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pageable, container, false);
        setupSwipeContainer(view);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        switch (type) {
            case NEWS:
                VolleyNews.getInstance().getNewsOverview();
                adapter = new NewsItemAdapter(new ArrayList<NewsItem>());
                MainActivity.currentScreen = MainActivity.Screen.NEWS;
                break;
            case AGENDA:
                VolleyAgenda.getInstance().getAgendaNewer();
                adapter = new AgendaItemAdapter(new ArrayList<AgendaItem>());
                MainActivity.currentScreen = MainActivity.Screen.AGENDA;
                break;
            case ROEPHOEK:
                VolleyRoephoek.getInstance().getNewer(1000);
                adapter = new RoephoekItemAdapter(new ArrayList<RoephoekItem>());
                MainActivity.currentScreen = MainActivity.Screen.ROEPHOEK;
                break;
        }
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (type) {
                    case NEWS:
                        VolleyNews.getInstance().getNewsOverview();
                        break;
                    case AGENDA:
                        VolleyAgenda.getInstance().getAgendaNewer();
                        break;
                    case ROEPHOEK:
                        VolleyRoephoek.getInstance().getOlder(100000);
                        break; // TODO: 5/16/16 gebruik juiste id
                }
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);
    }

    // EVENT HANDLING
    public void onEventMainThread(NewsOverviewEvent event) {
        swipeContainer.setRefreshing(false);
        if (adapter instanceof NewsItemAdapter) {
            adapter.update(event.newsOverview.getContent());
        }
    }

    public void onEventMainThread(AgendaEvent event) {
        swipeContainer.setRefreshing(false);
        if (adapter instanceof AgendaItemAdapter) {
            adapter.update(event.agenda.getContent());
        }
    }

    public void onEventMainThread(RoephoekEvent event) {
        swipeContainer.setRefreshing(false);
        if (adapter instanceof RoephoekItemAdapter) {
            adapter.update(event.roephoek.getContent());
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
