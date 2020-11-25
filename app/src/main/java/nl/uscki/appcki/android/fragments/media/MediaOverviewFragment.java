package nl.uscki.appcki.android.fragments.media;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.MediaCollectionAdapter;
import nl.uscki.appcki.android.generated.media.MediaCollection;

public class MediaOverviewFragment extends PageableFragment<MediaCollectionAdapter.ViewHolder, MediaCollection> {

    private List<MediaCollection> parentCollections;
    public static final String ARG_COLLECTION_PARENTS = "PARENT_COLLECTIONS";
    public static final String ARG_COLLECTION_ID = "COLLECTION_ID";

    public MediaOverviewFragment() {
        // Required empty public constructor
        Log.e(getTag(), "Started MediaOverviewFragment");
    }

    private final int MEDIA_PAGE_SIZE = 10;
    private int parentCollection = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.parentCollection = getArguments().getInt(ARG_COLLECTION_ID, 0);
            this.parentCollections = getArguments().getParcelableArrayList(ARG_COLLECTION_PARENTS);
            Log.e(getTag(), "Found collection ID " + this.parentCollection);
        } else {
            Log.e(getTag(), "No collection ID passed");
        }

        if(this.parentCollections == null)
            this.parentCollections = Collections.emptyList();

        Log.e(getTag(), this.parentCollections.size() + " parents");

        setHasOptionsMenu(true);

        setAdapter(new MediaCollectionAdapter(new ArrayList<>(), getActivity(), parentCollections));
        Services.getInstance().mediaService.getCollection(parentCollection, page, MEDIA_PAGE_SIZE).enqueue(callback);
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
}
