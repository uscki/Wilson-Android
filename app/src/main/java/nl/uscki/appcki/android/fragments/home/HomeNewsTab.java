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
import nl.uscki.appcki.android.fragments.adapters.NewsItemAdapter;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.generated.news.NewsOverview;

/**
 * Created by peter on 11/23/16.
 */

public class HomeNewsTab extends PageableFragment<NewsOverview> {
    private final int NEWS_PAGE_SIZE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new NewsItemAdapter(new ArrayList<NewsItem>()));
        Services.getInstance().newsService.older(page, NEWS_PAGE_SIZE).enqueue(callback);

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
        return NEWS_PAGE_SIZE;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().newsService.older(page, NEWS_PAGE_SIZE).enqueue(callback);
    }

    public void onScrollRefresh() {
        if(!tinyPage)
            Services.getInstance().newsService.older(page, NEWS_PAGE_SIZE).enqueue(callback);
    }
}
