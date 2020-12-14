package nl.uscki.appcki.android.fragments.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.ContentLoadedEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.MediaCollectionAdapter;
import nl.uscki.appcki.android.fragments.media.adapters.BreadcrumbAdapter;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaCollectionMember;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.helpers.ISharedElementViewContainer;
import retrofit2.Response;

public class MediaCollectionFragment extends PageableFragment<MediaCollectionAdapter.MediaCollectionMemberViewHolder, MediaCollectionMember> implements ISharedElementViewContainer {

    private List<MediaCollection> parentCollections;
    private MediaCollection collection;
    private MediaCollectionAdapter adapter;

    StaggeredGridLayoutManager mediaGridLayoutManager;

    public static final String ARG_COLLECTION_PARENTS = "PARENT_COLLECTIONS";
    public static final String ARG_COLLECTION_ID = "COLLECTION_ID";

    public MediaCollectionFragment() {
        // Required empty public constructor
    }

    private final int MEDIA_PAGE_SIZE = 10;
    private int collectionID = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO add this as shared element callback fragment to MainActivity (via getActivity())

        if(getArguments() != null) {
            this.collectionID = getArguments().getInt(ARG_COLLECTION_ID, 0);
            this.parentCollections = getArguments().getParcelableArrayList(ARG_COLLECTION_PARENTS);
        }

        if(this.parentCollections == null) {
            this.parentCollections = Collections.emptyList();
            this.collection = null;
        } else {
            this.collection = this.parentCollections.get(this.parentCollections.size() - 1);
            this.collectionID = this.collection.getId();
        }

        setHasOptionsMenu(true);

        this.adapter = new MediaCollectionAdapter(getActivity(), this, this.collection, new ArrayList<>(), parentCollections);
        setAdapter(adapter);

        Services.getInstance().mediaService.getCollection(collectionID, page, MEDIA_PAGE_SIZE).enqueue(collectionCallback);

        if(collection == null || collection.getNumOfPhotos() > 0) {
            Services.getInstance().mediaService
                    .photos(collectionID, 0, collection == null ? Integer.MAX_VALUE : collection.numOfPhotos)
                    .enqueue(metadataCallback);
        } else {
            mediaLoaded = true;
        }

        setupToolbar();
    }

    private void setupToolbar() {
        BasicActivity activity = (BasicActivity) getActivity();
        if(activity != null) {
            ActionBar bar = activity.getSupportActionBar();
            if(bar != null && collection != null) {
                bar.setTitle(collection.name);
                bar.setSubtitle(String.format(Locale.getDefault(), "Toegevoegd op %s", collection.getDateAdded().toString("d MMMM Y"))); // TODO string resources
            } else if (bar != null) {
                bar.setTitle("Media"); // TODO string resources
                bar.setSubtitle(null);
            }
        }
    }

    private Callback<Pageable<MediaCollection>> collectionCallback = new Callback<Pageable<MediaCollection>>() {
        @Override
        public void onSucces(Response<Pageable<MediaCollection>> response) {
            Log.v("PageableFragment", "Loaded collections: " + requestUrl + (response.body() == null ? "" : " " + response.body().getNumberOfElements() + " items loaded"));
            boolean otherLoaded;
            synchronized (mutex) {
                otherLoaded = mediaLoaded;
            }
            processCallback(response, MediaCollection.class, true, otherLoaded);
        }
    };

    private Callback<Pageable<MediaFileMetaData>> metadataCallback = new Callback<Pageable<MediaFileMetaData>>() {
        @Override
        public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
            Log.v("PageableFragment", "Loaded media files:: " + requestUrl + (response.body() == null ? "" : " " + response.body().getNumberOfElements() + " items loaded"));
            boolean otherLoaded;
            synchronized (mutex) {
                otherLoaded = collectionLoaded;
            }
            processCallback(response, MediaFileMetaData.class, otherLoaded, true);
        }
    };

    private final Object mutex = new Object();
    private boolean mediaLoaded = false;
    private boolean collectionLoaded = false;
    private boolean collectionNoMoreItems = false;

    private <T extends MediaCollectionMember> void processCallback(Response<Pageable<T>> response, Class<? extends MediaCollectionMember> type, boolean collectionLoaded, boolean mediaLoaded) {
        boolean isMediaEmpty;
        synchronized (mutex) {
            isMediaEmpty = (MediaFileMetaData.class.equals(type) &&
                    (response.body() == null || response.body().getNumberOfElements() == 0)) ||
                    (this.mediaLoaded && adapter.getFiles().isEmpty());
        }
        boolean isCollectionsEmpty =
                (
                    MediaCollection.class.equals(type) &&
                    (
                            (response.body() == null && adapter.getCollections().isEmpty()) ||
                            (response.body() != null && response.body().getNumberOfElements() == 0 && response.body().getFirst())
                    ) ||
                    (collectionNoMoreItems && adapter.getCollections().isEmpty())
                );

        if(refresh) {
            refresh = !(mediaLoaded && collectionLoaded);
            synchronized (mutex) {
                this.mediaLoaded = mediaLoaded;
                this.collectionLoaded = collectionLoaded;
            }
            noMoreContent = false; // reset noMoreContent because we are loading the first page
            scrollLoad = false; // reset scrollLoad because we are loading the first page
            swipeContainer.setRefreshing(refresh);

            if (response.body() != null) {
                if(isMediaEmpty && isCollectionsEmpty) {
                    // empty first page meaning there are no elements at all, also not on other pages
                    emptyTextScrollview.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    noMoreContent = true;
                } else {
                    emptyTextScrollview.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if(type.equals(MediaCollection.class)) {
                        collectionNoMoreItems = response.body().getNumberOfElements() < getPageSize();
                    }
                    noMoreContent = collectionNoMoreItems && mediaLoaded;
                }

                Log.v("pageablefragment", "clearing items and adding this page");
                if(MediaCollection.class.equals(type)) {
                    adapter.addChildCollections((List<MediaCollection>) response.body().getContent());
                } else if(MediaFileMetaData.class.equals(type)) {
                    adapter.addMediaFiles((List<MediaFileMetaData>) response.body().getContent());
                }
            } else {
                //TODO handle failing to load more
                Log.e("PageableFragment", "something failed: " + response.body());
            }
        } else if(scrollLoad) {
            if(MediaCollection.class.equals(type)) {
                scrollLoad = false;
                noMoreContent = false;
            }
            if(response.body() != null) {
                Log.v("pageablefragment", "totalpages: " + response.body().getTotalPages());
                if(isMediaEmpty && isCollectionsEmpty) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    noMoreContent = true;
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    noMoreContent = response.body().getNumberOfElements() < getPageSize();
                }

                Log.e("pageablefragment", "adding items to the bottom");
                if(MediaCollection.class.equals(type)) {
                    adapter.addChildCollections((List<MediaCollection>) response.body().getContent());
                } else if(MediaFileMetaData.class.equals(type)) {
                    adapter.addMediaFiles((List<MediaFileMetaData>) response.body().getContent());
                }
                getAdapter().showLoadingMoreItems(false);
            }
        }

        // Notify who-ever might be interested that new items have been loaded
        EventBus.getDefault().post(new ContentLoadedEvent(MediaCollectionFragment.this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.MEDIA_COLLECTION_OVERVIEW;

        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.mediaGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        this.mediaGridLayoutManager.setItemPrefetchEnabled(true);
        this.mediaGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(this.mediaGridLayoutManager);

        RecyclerView breadcrumbs = view.findViewById(R.id.media_breadcrumbs);
        breadcrumbs.setAdapter(new BreadcrumbAdapter(parentCollections == null ? Collections.emptyList() : parentCollections));

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_media_collection;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipeRefresh() {
        adapter.clear();
        synchronized (mutex) {
            mediaLoaded = false;
            collectionLoaded = false;
        }
        collectionNoMoreItems = false;
        Services.getInstance().mediaService.getCollection(collectionID, page, MEDIA_PAGE_SIZE).enqueue(collectionCallback);
        Services.getInstance().mediaService.photos(collectionID, 0, collection == null ? Integer.MAX_VALUE : collection.getNumOfPhotos()).enqueue(metadataCallback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().mediaService.getCollection(collectionID, page, MEDIA_PAGE_SIZE).enqueue(collectionCallback);
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
        setupToolbar();
    }

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        if(!names.isEmpty() && names.get(0) != null && this.mediaGridLayoutManager != null) {
            String transitionName = names.get(0);
            for(int i = 0; i < this.adapter.getItemCount(); i++) {
                View view = this.mediaGridLayoutManager.findViewByPosition(i);
                if(view != null) {
                    View imageView = view.findViewById(R.id.media_grid_photo);
                    if(imageView != null && transitionName.equals(imageView.getTransitionName())) {
                        sharedElements.put(transitionName, imageView);
                        if(getActivity() != null) getActivity().startPostponedEnterTransition();
                        return;
                    }
                }
            }
        }
    }

    private View getViewAt(int position) {
        View containerView = null;
        if(this.mediaGridLayoutManager != null) {
            containerView = this.mediaGridLayoutManager.findViewByPosition(position);
        }
        if(containerView != null) {
            return containerView.findViewById(R.id.media_grid_photo);
        }
        return null;
    }

    @Override
    public int activityReentering(int code, Intent data) {
        code--;
        if(getViewAt(code) == null) {
            if(getActivity() != null) getActivity().postponeEnterTransition();
            this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(startPostponedIfEverythingElseFails);
        }
        this.recyclerView.scrollToPosition(((MediaCollectionAdapter)getAdapter()).getPositionInMediaList(code));
        return code;
    }

    private ViewTreeObserver.OnGlobalLayoutListener startPostponedIfEverythingElseFails = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Force the layout to continue in the rare case the view was null, but the sharedElementsMap was not updated
            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            Log.v("MediaCollection", "Starting postponed enter transition as backup, because the view has been updated");
            if(getActivity() != null) getActivity().startPostponedEnterTransition();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        BasicActivity activity = (BasicActivity) getActivity();
        if(activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.app_name);
            activity.getSupportActionBar().setSubtitle(null);
        }
    }

    @Override
    protected void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mediaGridLayoutManager.getItemCount();
                int[] lastVisibleCollections = mediaGridLayoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItem = 0;
                if(lastVisibleCollections.length > 0) {
                    lastVisibleItem = lastVisibleCollections[0];
                }

                if (!scrollLoad // we are not already loading a new page
                        && totalItemCount <= (lastVisibleItem + 1) // we should be loading a new page
                        && !noMoreContent) { // there is still content to load
                    // End has been reached
                    scrollLoad = true;
                    getAdapter().showLoadingMoreItems(true);
                    Log.v("PageableFragment", "Loading page: " + page);
                    page++; // update page
                    onScrollRefresh(); // and call
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public static class IntentBuilder {
        private MediaCollection collection;
        private int collectionID;
        private MediaCollection directParent;
        private ArrayList<MediaCollection> parents;

        public IntentBuilder(int collectionID) {
            this.collectionID = collectionID;
        }

        public IntentBuilder(@NonNull MediaCollection collection) {
            this.collection = collection;
            this.collectionID = collection.getId();
        }

        public IntentBuilder setDirectParent(MediaCollection directParent) {
            this.directParent = directParent;
            return this;
        }

        public IntentBuilder setParents(ArrayList<MediaCollection> parents) {
            this.parents = parents;
            return this;
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(MainActivity.ACTION_VIEW_COLLECTION);
            intent.putExtra(MediaCollectionFragment.ARG_COLLECTION_ID, this.collectionID);
            ArrayList<MediaCollection> parents = new ArrayList<>();
            if(this.parents != null) {
                parents.addAll(this.parents);
            }
            if(this.directParent != null) {
                parents.add(this.directParent);
            }
            if(this.collection != null) {
                parents.add(this.collection);
            }
            if(!parents.isEmpty()) {
                intent.putParcelableArrayListExtra(MediaCollectionFragment.ARG_COLLECTION_PARENTS, parents);
            }
            return intent;
        }
    }
}
