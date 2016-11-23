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
import nl.uscki.appcki.android.fragments.adapters.NewsItemAdapter;
import nl.uscki.appcki.android.generated.news.NewsItem;
import nl.uscki.appcki.android.generated.news.NewsOverview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 11/23/16.
 */

public class HomeNewsTab extends PageableFragment {
    private final int NEWS_PAGE_SIZE = 3;

    private Callback<NewsOverview> newsOverviewCallback = new Callback<NewsOverview>() {
        @Override
        public void onResponse(Call<NewsOverview> call, Response<NewsOverview> response) {
            swipeContainer.setRefreshing(false);
            if (loading) {
                loading = false;
                if (getAdapter() instanceof NewsItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().addItems(response.body().getContent());
                    } else {
                        //TODO handle failing to load more
                        Log.d("HomeSubFragments", response.body()+"");
                    }
                }
            }else {
                if (getAdapter() instanceof NewsItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().update(response.body().getContent());
                    }
                }
            }

        }

        @Override
        public void onFailure(Call<NewsOverview> call, Throwable t) {
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

        setAdapter(new NewsItemAdapter(new ArrayList<NewsItem>()));
        Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
        MainActivity.currentScreen = MainActivity.Screen.NEWS;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
    }

    public void onScrollRefresh() {
        Services.getInstance().newsService.overview(page, NEWS_PAGE_SIZE).enqueue(newsOverviewCallback);
    }
}
