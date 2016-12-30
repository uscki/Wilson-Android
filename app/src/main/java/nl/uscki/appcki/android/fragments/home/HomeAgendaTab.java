package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.AgendaItemAdapter;
import nl.uscki.appcki.android.generated.agenda.Agenda;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 11/23/16.
 */

public class HomeAgendaTab extends PageableFragment<Agenda> {
    private final int AGENDA_PAGE_SIZE = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new AgendaItemAdapter(new ArrayList<AgendaItem>()));
        Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(callback);

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
    protected int getPageSize() {
        return AGENDA_PAGE_SIZE;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        if(!tinyPage)
            Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(callback);
    }
}
