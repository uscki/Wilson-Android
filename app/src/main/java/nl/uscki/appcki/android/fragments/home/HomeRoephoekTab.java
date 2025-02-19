package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.RoephoekItemAdapter;
import nl.uscki.appcki.android.fragments.shoutbox.NewShoutWidget;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 11/23/16.
 */

public class HomeRoephoekTab extends PageableFragment<RoephoekItemAdapter.ViewHolder, RoephoekItem> {
    private final int ROEPHOEK_PAGE_SIZE = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if(getAdapter() == null) {
            setAdapter(new RoephoekItemAdapter(new ArrayList<>()));
            Services.getInstance().shoutboxService.getShoutsCollection(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = setFabEnabled(true);
        if(fab != null) {
            fab.setOnClickListener(v -> addNewPageableItemWidget(new NewShoutWidget(), true));
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
        Services.getInstance().shoutboxService.getShoutsCollection(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().shoutboxService.getShoutsCollection(page, ROEPHOEK_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.roephoek_no_new_shouts);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getAdapter().getItemCount() > 0)
            this.swipeContainer.setRefreshing(false);
    }
}
