package nl.uscki.appcki.android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.ConnectException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.events.RoephoekEvent;
import nl.uscki.appcki.android.fragments.adapters.AgendaItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.NewsItemAdapter;
import nl.uscki.appcki.android.fragments.adapters.RoephoekItemAdapter;
import nl.uscki.appcki.android.generated.agenda.Agenda;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.generated.news.NewsOverview;
import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private final int NEWS_PAGE_SIZE = 3;
    private final int AGENDA_PAGE_SIZE = 4;
    private final int ROEPHOEK_PAGE_SIZE = 7;

    private static boolean tinyPage = false;

    private Callback<NewsOverview> newsOverviewCallback = new Callback<NewsOverview>() {
        @Override
        public void onResponse(Call<NewsOverview> call, Response<NewsOverview> response) {
            swipeContainer.setRefreshing(false);
            if (loading) {
                loading = false;
                if (getAdapter() instanceof NewsItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().addItems(response.body().getContent());
                    } else {
                        //TODO handle failing to load more
                        Log.d("HomeSubFragments", response.body()+"");
                    }
                }
            }else {
                if (getAdapter() instanceof NewsItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().update(response.body().getContent());
                    }
                }
            }

        }

        @Override
        public void onFailure(Call<NewsOverview> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

    private Callback<Agenda> agendaCallback = new Callback<Agenda>() {
        @Override
        public void onResponse(Call<Agenda> call, Response<Agenda> response) {
            swipeContainer.setRefreshing(false);
            if(loading) {
                loading = false;
                if (getAdapter() instanceof AgendaItemAdapter) {
                    if (response.body() != null) {
                        if(response.body().getNumberOfElements() < AGENDA_PAGE_SIZE) {
                            tinyPage = true;
                            Log.e("HomeSubFragments", "tinypage: " + tinyPage);
                        }
                        getAdapter().addItems(response.body().getContent());
                    } else {
                        //TODO handle failing to load more
                        Log.e("HomeSubFragments", "something failed: " + response.body());
                    }
                }
            } else {
                if (getAdapter() instanceof AgendaItemAdapter) {
                    Log.e("HomeSubFragments", "update: " + response.body());
                    if(response.body() != null) {
                        if(response.body().getNumberOfElements() < AGENDA_PAGE_SIZE) {
                            tinyPage = true;
                            Log.e("HomeSubFragments", "tinypage: " + tinyPage);
                        }
                        getAdapter().update(response.body().getContent());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<Agenda> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

    private Callback<Roephoek> roephoekCallback = new Callback<Roephoek>() {
        @Override
        public void onResponse(Call<Roephoek> call, Response<Roephoek> response) {
            swipeContainer.setRefreshing(false);
            if(loading) {
                loading = false;
                if (getAdapter() instanceof RoephoekItemAdapter) {
                    getAdapter().addItems(response.body().getContent());
                }
            } else {
                if (getAdapter() instanceof RoephoekItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().update(response.body().getContent());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<Roephoek> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

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
                setAdapter(new NewsItemAdapter(new ArrayList<NewsItem>()));
                Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
                MainActivity.currentScreen = MainActivity.Screen.NEWS;
                break;
            case AGENDA:
                setAdapter(new AgendaItemAdapter(new ArrayList<AgendaItem>()));
                Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(agendaCallback);
                MainActivity.currentScreen = MainActivity.Screen.AGENDA;
                break;
            case ROEPHOEK:
                setAdapter(new RoephoekItemAdapter(new ArrayList<RoephoekItem>()));
                Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, 1000000).enqueue(roephoekCallback);
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
                Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
                break;
            case AGENDA:
                Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(agendaCallback);
                break;
            case ROEPHOEK:
                Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, 1000000).enqueue(roephoekCallback);
                break;
        }
    }

    @Override
    public void onScrollRefresh() {
        switch(type) {
            case NEWS:
                Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
                break;
            case AGENDA:
                if(!tinyPage)
                    Services.getInstance().agendaService.older(page, AGENDA_PAGE_SIZE, getAdapter().getLastID()).enqueue(agendaCallback);
                break;
            case ROEPHOEK:
                Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, getAdapter().getLastID()).enqueue(roephoekCallback);
                break;
        }
    }

    // EVENT HANDLING
    public void onEventMainThread(RoephoekEvent event) {
        // laad page 0 als we een nieuwe roep hebben geplaatst
        if (event.roephoek) {
            switch (type) {
                case NEWS:
                    break;
                case AGENDA:// ook bij agenda, want dan is roephoek misschien niet zichtbaar maar wel geladen
                    Services.getInstance().shoutboxService.older(0, ROEPHOEK_PAGE_SIZE, 1000000).enqueue(roephoekCallback);
                    break;
                case ROEPHOEK:
                    Services.getInstance().shoutboxService.older(0, ROEPHOEK_PAGE_SIZE, 1000000).enqueue(roephoekCallback);
                    break;
            }
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
