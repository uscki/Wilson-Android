package me.blackwolf12333.appcki.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import me.blackwolf12333.appcki.events.RoephoekOlderEvent;
import me.blackwolf12333.appcki.fragments.adapters.AgendaItemAdapter;
import me.blackwolf12333.appcki.fragments.adapters.NewsItemAdapter;
import me.blackwolf12333.appcki.fragments.adapters.RoephoekItemAdapter;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;
import me.blackwolf12333.appcki.generated.news.NewsItem;
import me.blackwolf12333.appcki.generated.roephoek.RoephoekItem;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class HomeSubFragments extends PageableFragment {
    private int type;

    public static Menu m;

    public static final int NEWS = 0;
    public static final int AGENDA = 1;
    public static final int ROEPHOEK = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeSubFragments() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        Log.d("HomeSubFragments", "type=" + type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        switch (type) {
            case NEWS:
                VolleyNews.getInstance().getNewsOverview();
                setAdapter(new NewsItemAdapter(new ArrayList<NewsItem>()));
                MainActivity.currentScreen = MainActivity.Screen.NEWS;
                break;
            case AGENDA:
                VolleyAgenda.getInstance().getAgendaNewer();
                setAdapter(new AgendaItemAdapter(new ArrayList<AgendaItem>()));
                MainActivity.currentScreen = MainActivity.Screen.AGENDA;
                break;
            case ROEPHOEK:
                VolleyRoephoek.getInstance().getOlder(1000000);
                setAdapter(new RoephoekItemAdapter(new ArrayList<RoephoekItem>()));
                MainActivity.currentScreen = MainActivity.Screen.ROEPHOEK;
                break;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        switch (type) {
            case NEWS:
                inflater.inflate(R.menu.main, menu);
                break;
            case AGENDA:
                inflater.inflate(R.menu.agenda_menu, menu);
                // verander visibility pas als we in een detail view zitten
                menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
                menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);
                break;
            case ROEPHOEK:
                inflater.inflate(R.menu.roephoek_menu, menu);
                break;
            default:
                inflater.inflate(R.menu.main, menu);
        }

        m = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipeRefresh() {
        switch (type) {
            case NEWS:
                VolleyNews.getInstance().getNewsOverview();
                break;
            case AGENDA:
                VolleyAgenda.getInstance().getAgendaNewer();
                break;
            case ROEPHOEK:
                VolleyRoephoek.getInstance().getOlder(100000);
                break;
        }
    }

    @Override
    public void onScrollRefresh() {
        switch(type) {
            case NEWS:
                break;
            case AGENDA:
                VolleyAgenda.getInstance().getAgendaOlder(getAdapter().getLastID());
                break;
            case ROEPHOEK:
                VolleyRoephoek.getInstance().getOlder(getAdapter().getLastID());
                break;
        }
    }

    // EVENT HANDLING

    public void onEventMainThread(RoephoekOlderEvent event) {
        swipeContainer.setRefreshing(false);
        if(loading) {
            loading = false;
            if (getAdapter() instanceof RoephoekItemAdapter) {
                getAdapter().addItems(event.roephoek.getContent());
            }
        } else {
            if (getAdapter() instanceof RoephoekItemAdapter) {
                getAdapter().update(event.roephoek.getContent());
            }
        }
    }

    public void onEventMainThread(AgendaEvent event) {
        swipeContainer.setRefreshing(false);
        if(loading) {
            loading = false;
            if (getAdapter() instanceof AgendaItemAdapter) {
                getAdapter().addItems(event.agenda.getContent());
            }
        } else {
            if (getAdapter() instanceof AgendaItemAdapter) {
                getAdapter().update(event.agenda.getContent());
            }
        }

    }

    public void onEventMainThread(NewsOverviewEvent event) {
        swipeContainer.setRefreshing(false);
        if (getAdapter() instanceof NewsItemAdapter) {
            getAdapter().update(event.newsOverview.getContent());
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
