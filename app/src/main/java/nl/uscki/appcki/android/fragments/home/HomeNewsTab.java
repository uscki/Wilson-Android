package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.SwitchTabEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.NewsItemAdapter;
import nl.uscki.appcki.android.generated.news.NewsItem;

/**
 * Created by peter on 11/23/16.
 */

public class HomeNewsTab extends PageableFragment<NewsItemAdapter.ViewHolder, NewsItem> {
    private final int NEWS_PAGE_SIZE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if(getAdapter() == null) {
            setAdapter(new NewsItemAdapter(new ArrayList<>()));
            Services.getInstance().newsService.getNewsCollection(page, NEWS_PAGE_SIZE).enqueue(callback);
        }

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
        Services.getInstance().newsService.getNewsCollection(page, NEWS_PAGE_SIZE).enqueue(callback);
    }

    public void onScrollRefresh() {
        Services.getInstance().newsService.getNewsCollection(page, NEWS_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.news_no_new_news);
    }

    public void onEventMainThread(SwitchTabEvent event) {
        if(event.itemIdToScrollTo >= 0)
            scrollToItemWithId(event.itemIdToScrollTo, true);
    }

    /**
     * Try to scroll to the given item. Show an error message if the item cannot be found
     * in the list of loaded items
     * @param id                    Id of the item to scroll to
     * @param showErrorOnFailure    True if an error should be shown on failure
     * @return                      Success status
     */
    public boolean scrollToItemWithId(int id, boolean showErrorOnFailure) {
        if(scrollToItem(id)) {
            return true;
        } else if(showErrorOnFailure) {
                Toast.makeText(
                        App.getContext(),
                        getResources().getString(R.string.main_failure_to_show_newsitem),
                        Toast.LENGTH_LONG)
                        .show();
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getAdapter().getItemCount() > 0)
            this.swipeContainer.setRefreshing(false);
    }
}
