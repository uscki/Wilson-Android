package nl.uscki.appcki.android.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.views.ANewPageableItem;
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
    private boolean noMoreContent;
    protected boolean refresh;
    private boolean scrollLoad;

    protected Callback<T> callback = new Callback<T>() {
        @Override
        public void onSucces(Response<T> response) {
            if(refresh) {
                refresh = false;
                noMoreContent = false; // reset noMoreContent because we are loading the first page
                scrollLoad = false; // reset scrollLoad because we are loading the first page
                swipeContainer.setRefreshing(false);

                Log.e("PageableFragment", "Reload update: " + requestUrl);
                if (response.body() != null) {
                    // empty first page meaning there are no elements at all, also not on other pages
                    if(response.body().getNumberOfElements() == 0 && response.body().getFirst()) {
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        noMoreContent = true;
                    } else {
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        noMoreContent = response.body().getNumberOfElements() < getPageSize();
                    }

                    Log.e("pageablefragment", "clearing items and adding this page");
                    getAdapter().update(response.body().getContent());
                } else {
                    //TODO handle failing to load more
                    Log.e("PageableFragment", "something failed: " + response.body());
                }
            } else if(scrollLoad) {
                scrollLoad = false;
                noMoreContent = false;
                Log.e("PageableFragment", "Scroll update: " +  requestUrl);
                if(response.body() != null) {
                    Log.e("pageablefragment", "totalpages: " + response.body().getTotalPages());
                    if(response.body().getNumberOfElements() == 0 && response.body().getFirst()) {
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        noMoreContent = true;
                    } else {
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        noMoreContent = response.body().getNumberOfElements() < getPageSize();
                    }

                    Log.e("pageablefragment", "adding items to the bottom");
                    getAdapter().addItems(response.body().getContent());
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

        refresh = true; // always start with a refreshing view
        scrollLoad = false;
        return view;
    }

    public void scrollToItem(int id) {
        int itemPosition = adapter.getItemPosition(id);
        if(itemPosition < 1)
            return;

        recyclerView.scrollToPosition(itemPosition);
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

                if (!scrollLoad // we are not already loading a new page
                        && totalItemCount <= (lastVisibleItem + visibleThreshold) // we should be loading a new page
                        && !noMoreContent) { // there is still content to load
                    // End has been reached
                    scrollLoad = true;
                    Log.e("PageableFragment", "Loading page: " + page);
                    page++; // update page
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
                refresh = true;
                onSwipeRefresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(true);
    }

    public void refresh() {
        if(swipeContainer != null) {
            page = 0;
            refresh = true;
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                }
            });

            if(recyclerView != null) {
                recyclerView.scrollToPosition(0);
            }
        }
    }

    public BaseItemAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseItemAdapter adapter) {
        this.adapter = adapter;
    }

    protected FloatingActionButton setFabEnabled(@NonNull View view, boolean enabled) {
        FloatingActionButton fab = view.findViewById(R.id.pageableFloatingActionButton);
        if(fab == null) {
            Log.e(
                    getClass().getSimpleName(),
                    "Trying to enable Fabulous action button, but not found");
            return null;
        }

        fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
        fab.setClickable(enabled);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fab.setFocusable(enabled);
        }

        // Use the return value to set the onClick action
        return fab;
    }

    public void addNewPageableItemWidget(ANewPageableItem widget) {
        widget.setParent(this);
        FragmentManager fm = getSupportFragmentManager();
        if(fm == null) return;

        fm.beginTransaction()
                .replace(R.id.new_item_placeholder, widget)
                .commit();

        View view = getView();
        if(view != null)
            setFabEnabled(getView(), false);
    }

    public void removeNewPageableItemWidget() {
        FragmentManager fm = getSupportFragmentManager();
        if(fm == null) return;

        fm.beginTransaction()
                .replace(R.id.new_item_placeholder, new Fragment())
                .commit();

        View view = getView();
        if(view != null)
            setFabEnabled(view, true);
    }

    private FragmentManager getSupportFragmentManager() {
        FragmentActivity activity = getActivity();
        if(activity == null) return null;

        return activity.getSupportFragmentManager();
    }

    public abstract void onSwipeRefresh();
    public abstract void onScrollRefresh();
    public abstract String getEmptyText();
    protected abstract int getPageSize();

    public void onEventMainThread(ErrorEvent e) {
        swipeContainer.setRefreshing(false);
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noMoreContent = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
