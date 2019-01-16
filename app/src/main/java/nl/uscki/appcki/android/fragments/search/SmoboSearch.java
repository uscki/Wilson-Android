package nl.uscki.appcki.android.fragments.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.SmoboSearchResultAdapter;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;
import nl.uscki.appcki.android.generated.smobo.SmoboSearchPage;

/**
 * Created by peter on 4/24/17.
 */

public class SmoboSearch extends PageableFragment<SmoboSearchPage> {
    private final int PAGE_SIZE = 10;
    private String query;
    private BasicActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAdapter(new SmoboSearchResultAdapter(new ArrayList<PersonSimpleName>()));

        // do this after setAdapter because super.onCreateView has a dependency on the adapter set there
        // and do this before setRefreshing because this creates the swipeContainer
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // search doesn't start with a loading view, it has to wait for user input
        swipeContainer.setRefreshing(false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.appbar_search_menu, menu);
        menu.findItem(R.id.search_view).setVisible(true);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryHint("Menno Veen");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SmoboSearch.this.query = query;
                page = 0;
                swipeContainer.setRefreshing(true);
                refresh = true; // the callback needs to know this
                Services.getInstance().smoboService.search(query, page, getPageSize()).enqueue(callback);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SmoboSearch.this.query = newText;
                return false;
            }
        });
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.search_no_results);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().smoboService.search(query, page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().smoboService.search(query, page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof BasicActivity) {
            activity = (BasicActivity) context;
        }
        super.onAttach(context);
    }
}
