package nl.uscki.appcki.android.fragments.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.MediaCollectionAdapter;
import nl.uscki.appcki.android.generated.media.MediaCollection;

public class MediaOverviewFragment extends PageableFragment<MediaCollectionAdapter.ViewHolder, MediaCollection> {

    private ArrayList<MediaCollection> parentCollections;
    private MediaCollectionAdapter adapter;
    public static final String ARG_COLLECTION_PARENTS = "PARENT_COLLECTIONS";
    public static final String ARG_COLLECTION_ID = "COLLECTION_ID";

    public MediaOverviewFragment() {
        // Required empty public constructor
    }

    private final int MEDIA_PAGE_SIZE = 10;
    private int parentCollection = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.parentCollection = getArguments().getInt(ARG_COLLECTION_ID, 0);
            this.parentCollections = getArguments().getParcelableArrayList(ARG_COLLECTION_PARENTS);
        }

        if(this.parentCollections == null)
            this.parentCollections = new ArrayList<>(Collections.emptyList());

        setHasOptionsMenu(true);

        this.adapter = new MediaCollectionAdapter(new ArrayList<>(), parentCollections);
        setAdapter(adapter);
        Services.getInstance().mediaService.getCollection(parentCollection, page, MEDIA_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEDIA_COLLECTION_OVERVIEW;
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
        Services.getInstance().mediaService.getCollection(parentCollection, page, MEDIA_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().mediaService.getCollection(parentCollection, page, MEDIA_PAGE_SIZE).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return "Geen mediacollecties gevondeon"; // TODO put to strings
    }

    @Override
    protected int getPageSize() {
        return MEDIA_PAGE_SIZE;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.adapter.getItemCount() > 0)
            this.swipeContainer.setRefreshing(false);
    }
}
