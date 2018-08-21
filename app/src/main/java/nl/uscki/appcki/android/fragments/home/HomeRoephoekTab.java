package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.RoephoekItemAdapter;
import nl.uscki.appcki.android.fragments.shoutbox.NewShoutWidget;
import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 11/23/16.
 */

public class HomeRoephoekTab extends PageableFragment<Roephoek> {
    private final int ROEPHOEK_PAGE_SIZE = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new RoephoekItemAdapter(new ArrayList<RoephoekItem>()));
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = setFabEnabled(view, true);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewPageableItemWidget(new NewShoutWidget());
                    Log.e(getClass().getSimpleName(), "Clicked new shout button");
                }
            });
        }
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
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.roephoek_no_new_shouts);
    }
}
