package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private final int AGENDA_PAGE_SIZE = 5;

    boolean showArchive = false;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_agenda_archive) {
            showArchive = true;

            page = 0; // reset page in case user has scrolled with newer
            getAdapter().clear();
            Services.getInstance().agendaService.older(page, AGENDA_PAGE_SIZE).enqueue(callback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getPageSize() {
        return AGENDA_PAGE_SIZE;
    }

    @Override
    public void onSwipeRefresh() {
        showArchive = false;
        Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        if (showArchive) {
            Services.getInstance().agendaService.older(page, AGENDA_PAGE_SIZE).enqueue(callback);
        } else {
            Services.getInstance().agendaService.newer(page, AGENDA_PAGE_SIZE).enqueue(callback);
        }
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.agenda_no_new_agendas);
    }
}
