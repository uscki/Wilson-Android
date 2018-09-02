package nl.uscki.appcki.android.fragments.poll;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.PollAdapter;
import nl.uscki.appcki.android.generated.poll.PollItem;
import nl.uscki.appcki.android.generated.poll.PollPage;

/**
 * Created by peter on 3/7/17.
 */

public class PollOverviewFragment extends PageableFragment<PollPage> {
    private final int POLL_PAGE_SIZE = 14;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.POLL_OVERVIEW;
        setHasOptionsMenu(true);

        setAdapter(new PollAdapter(new ArrayList<PollItem>()));
        Services.getInstance().pollService.overview(page, getPageSize()).enqueue(callback);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getPageSize() {
        return POLL_PAGE_SIZE;
    }

    @Override
    public String getEmptyText() {
        return "todo"; // TODO: add empty text
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().pollService.overview(page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().pollService.overview(page, getPageSize()).enqueue(callback);
    }
}
