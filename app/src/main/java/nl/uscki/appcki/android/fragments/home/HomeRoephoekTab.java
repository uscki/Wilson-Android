package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.RoephoekEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.RoephoekItemAdapter;
import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 11/23/16.
 */

public class HomeRoephoekTab extends PageableFragment<Roephoek> {
    private final int ROEPHOEK_PAGE_SIZE = 7;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new RoephoekItemAdapter(new ArrayList<RoephoekItem>()));
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.roephoek_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getPageSize() {
        return ROEPHOEK_PAGE_SIZE;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        if(!tinyPage)
            Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.roephoek_no_new_shouts);
    }

    // EVENT HANDLING
    public void onEventMainThread(RoephoekEvent event) {
        page = 0;
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
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
