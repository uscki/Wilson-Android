package nl.uscki.appcki.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public abstract class PageableFragment<T extends Pageable> extends Fragment {

    private BaseItemAdapter adapter;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeContainer;
    protected TextView emptyText;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    protected boolean loading;

    // The current page
    protected Integer page;
    // Whether this page is too small for the requested size
    protected boolean tinyPage;

    protected Callback<T> callback = new Callback<T>() {
        @Override
        public void onSucces(Response<T> response) {
            swipeContainer.setRefreshing(false);
            if(loading) { // Refresh
                loading = false;
                if (response.body() != null) {
                    if(response.body().getNumberOfElements() == 0) {
                        //emptyText.setText(getEmptyText());
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        tinyPage = true;
                    } else {
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        tinyPage = response.body().getNumberOfElements() < getPageSize();
                    }

                    if(!tinyPage)
                        getAdapter().addItems(response.body().getContent());
                    else
                        getAdapter().update(response.body().getContent());
                    Log.e("PageableFragment", "tinypage: " + tinyPage);
                } else {
                    //TODO handle failing to load more
                    Log.e("PageableFragment", "something failed: " + response.body());
                }
            } else { // Scroll
                Log.e("PageableFragment", "update: " + response.body());
                if(response.body() != null) {
                    if(response.body().getNumberOfElements() == 0) {
                        emptyText.setText(getEmptyText());
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        tinyPage = true;
                    } else {
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        tinyPage = response.body().getNumberOfElements() < getPageSize();
                    }
                    Log.e("PageableFragment", "tinypage: " + tinyPage);
                    getAdapter().update(response.body().getContent());
                }
            }
        }
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PageableFragment() {
        page = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pageable, container, false);
        setupSwipeContainer(view);
        setupRecyclerView(view);

        emptyText = (TextView) view.findViewById(R.id.empty_text);
        emptyText.setText(getEmptyText());
        return view;
    }

    protected void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    loading = true;
                    page++; // update page
                    Log.e("PageableFragment", "Loading page: " + page);
                    onScrollRefresh(); // and call
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    protected void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                onSwipeRefresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);
    }

    public BaseItemAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseItemAdapter adapter) {
        this.adapter = adapter;
    }

    public abstract void onSwipeRefresh();
    public abstract void onScrollRefresh();
    public abstract String getEmptyText();
    protected abstract int getPageSize();
}
