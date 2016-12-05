package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.ConnectException;
import java.util.ArrayList;

import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.AgendaItemAdapter;
import nl.uscki.appcki.android.generated.agenda.Agenda;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 11/23/16.
 */

public class HomeAgendaTab extends PageableFragment {
    private final int AGENDA_PAGE_SIZE = 4;
    private static boolean tinyPage = false;

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
                        } else {
                            tinyPage = false;
                            getAdapter().addItems(response.body().getContent());
                        }
                        Log.e("HomeAgendaTab", "tinypage: " + tinyPage);
                    } else {
                        //TODO handle failing to load more
                        Log.e("HomeAgendaTab", "something failed: " + response.body());
                    }
                }
            } else {
                if (getAdapter() instanceof AgendaItemAdapter) {
                    Log.e("HomeAgendaTab", "update: " + response.body());
                    if(response.body() != null) {
                        if(response.body().getNumberOfElements() < AGENDA_PAGE_SIZE) {
                            tinyPage = true;
                        } else {
                            tinyPage = false;
                        }
                        Log.e("HomeAgendaTab", "tinypage: " + tinyPage);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new AgendaItemAdapter(new ArrayList<AgendaItem>()));
        Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(agendaCallback);
        MainActivity.currentScreen = MainActivity.Screen.AGENDA;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.agenda_menu, menu);
        // verander visibility pas als we in een detail view zitten
        menu.findItem(R.id.action_agenda_subscribe).setVisible(false);
        menu.findItem(R.id.action_agenda_unsubscribe).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(agendaCallback);
    }

    @Override
    public void onScrollRefresh() {
        if(!tinyPage)
            Services.getInstance().agendaService.older(page, AGENDA_PAGE_SIZE, getAdapter().getLastID()).enqueue(agendaCallback);
    }
}
