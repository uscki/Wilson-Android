package nl.uscki.appcki.android.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.events.ContentLoadedEvent;
import nl.uscki.appcki.android.events.ErrorEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.views.NewPageableItem;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public abstract class PageableFragment<T extends RecyclerView.ViewHolder, K extends IWilsonBaseItem> extends Fragment {

    public static final int NEW_ITEM_EDIT_BOX_POSITION_TOP = R.id.new_item_placeholder_top;
    public static final int NEW_ITEM_EDIT_BOX_POSITION_BOTTOM = R.id.new_item_placeholder_bottom;
    public static final int NEW_ITEM_EDIT_BOX_POSITION_DEFAULT = NEW_ITEM_EDIT_BOX_POSITION_TOP;
    public static final int LAST_ON_TOP = 30;
    public static final int FIRST_ON_TOP = 31;

    private BaseItemAdapter<T, K> adapter;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeContainer;
    protected TextView emptyText;
    protected NestedScrollView emptyTextScrollview;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    protected boolean loading;

    // The current page
    protected Integer page;
    protected boolean noMoreContent;
    protected boolean refresh;
    protected boolean scrollLoad;

    // Dealing with the FAB en refreshing and stuff
    private int editBoxPosition = NEW_ITEM_EDIT_BOX_POSITION_DEFAULT;
    private int scrollDirection = LAST_ON_TOP;

    protected Callback<Pageable<K>> callback = new Callback<Pageable<K>>() {
        @Override
        public void onSucces(Response<Pageable<K>> response) {
            if(refresh) {
                refresh = false;
                noMoreContent = false; // reset noMoreContent because we are loading the first page
                scrollLoad = false; // reset scrollLoad because we are loading the first page
                swipeContainer.setRefreshing(false);

                Log.e("PageableFragment", "Reload update: " + requestUrl);
                if (response.body() != null) {
                    // empty first page meaning there are no elements at all, also not on other pages
                    if(response.body().getNumberOfElements() == 0 && response.body().getFirst()) {
                        emptyTextScrollview.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        noMoreContent = true;
                    } else {
                        emptyTextScrollview.setVisibility(View.GONE);
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
                    getAdapter().showLoadingMoreItems(false);
                }
            }

            // Notify who-ever might be interested that new items have been loaded
            EventBus.getDefault().post(new ContentLoadedEvent(PageableFragment.this));
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

        emptyTextScrollview = view.findViewById(R.id.empty_text_scrollview);
        emptyText = (TextView) view.findViewById(R.id.empty_text);
        emptyText.setText(getEmptyText());

        refresh = true; // always start with a refreshing view
        scrollLoad = false;
        return view;
    }

    public boolean scrollToItem(int id) {
        if(id < 0)
            return true;

        int itemPosition = adapter.getItemPosition(id);

        if(itemPosition < 0)
            return false;

        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        if(llm == null) return false;

        llm.scrollToPositionWithOffset(itemPosition, 0);

        return true;
    }

    public void scrollToEnd() {
        if(recyclerView == null) return;
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
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
                    getAdapter().showLoadingMoreItems(true);
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
        swipeContainer.setNestedScrollingEnabled(getUseNestedScrolling());
    }

    public void refresh() {
        // Only refresh if more recent items appear on top. Otherwise,
        // new elements added by user should be handled in the callback
        if(scrollDirection == LAST_ON_TOP && swipeContainer != null) {
            page = 0;
            refresh = true;

            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    onSwipeRefresh();
                }
            });

            if(recyclerView != null) {
                recyclerView.scrollToPosition(0);
            }
        }
    }

    public BaseItemAdapter<T, K> getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseItemAdapter<T, K> adapter) {
        this.adapter = adapter;
    }

    protected void setEditBoxPosition(int position) {
        editBoxPosition = position;
    }

    /**
     * Set the default scroll direction of this fragment.
     * Either FIRST_ON_TOP, which indicates that on scrolling, more recent items are loaded, or
     * LAST_ON_TOP, which indicates that on scrolling, less recent items are loaded
     *
     * @param direction FIRST_ON_TOP|LAST_ON_TOP
     */
    protected void setScrollDirection(int direction) {
        scrollDirection = direction;
    }

    public FloatingActionButton setFabEnabled(@NonNull View view, boolean enabled) {
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

    /**
     * Add a new pageable item widget to the screen (set position with setEditBoxPosition())
     *
     * @param widget        The new pageable item widget to add
     * @param onlyWhenFab   If true, a FAB is shown instead of this fragment. Clicking the FAB
     *                      will make the fragment visible. Pressing back will make the fragment
     *                      invisible again and show the fab. If set to false, the fragment is
     *                      always visible and no fab is shown
     */
    public void addNewPageableItemWidget(NewPageableItem widget, boolean onlyWhenFab) {
        widget.setParent(this);
        FragmentManager fm = getChildFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(editBoxPosition, widget);
        if(onlyWhenFab) {
            widget.setFocusOnCreateView(true);
            ft.addToBackStack("new_item");
        }
        ft.commitAllowingStateLoss();

        View view = getView();
        if(onlyWhenFab && view != null) {
            setFabEnabled(getView(), false);
        }
    }

    /**
     * Remove the new item widget, and show the FAB again, so the process can repeat
     */
    public void removeNewPageableItemWidget() {
        FragmentManager fm = getChildFragmentManager();

        fm.beginTransaction()
                .replace(editBoxPosition, new Fragment())
                .commitAllowingStateLoss();

        View view = getView();
        if(view != null)
            setFabEnabled(view, true);
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

    /**
     * Override this method to return true to enable nested scrolling
     *
     * @return Boolean indicating if the SwipeRefreshLayout should use nested scrolling
     */
    protected boolean getUseNestedScrolling() {
        return false;
    }
}
