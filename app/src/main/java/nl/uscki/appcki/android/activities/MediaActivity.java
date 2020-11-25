package nl.uscki.appcki.android.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.media.MediaOverviewFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import retrofit2.Response;

public class MediaActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mediaGrid;
    StaggeredGridLayoutManager mediaGridLayoutManager;
    private PhotoGridAdapter mediaGridAdapter;

    private MediaCollection collection;

    private List<MediaCollection> parentCollections;
    private int collectionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Toolbar toolbar = findViewById(R.id.media_toolbar);

        Bundle args = getIntent().getExtras();

        if(args != null) {
            this.collectionID = args.getInt(MediaOverviewFragment.ARG_COLLECTION_ID, 0);
            this.parentCollections = args.getParcelableArrayList(MediaOverviewFragment.ARG_COLLECTION_PARENTS);
        } else {
            Log.v("MediaActivity", "No parent collections found. Assuming ROOT collection");
        }

        if(this.parentCollections == null) {
            this.parentCollections = Collections.emptyList();
            this.collection = null;
        } else {
            this.collection = this.parentCollections.get(this.parentCollections.size() - 1);
        }

        String toolbarTitle = "Mediacollecties"; // TODO string resources
        if(this.collection != null) {
            toolbarTitle = this.collection.name;
            toolbar.setSubtitle("Toegevoegd op " + this.collection.getDate().toString("d MMMM Y")); // TODO string resource
        }
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(collection == null || collection.getNumOfPhotos() == 0) {
            MediaOverviewFragment mediaOverviewFragment = new MediaOverviewFragment();
            mediaOverviewFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.media_fragment_overview_container, mediaOverviewFragment)
                    .commit();
        } else {
            findViewById(R.id.media_fragment_overview_container).setVisibility(View.GONE);
        }

        RecyclerView breadcrumbs = findViewById(R.id.media_breadcrumbs);
        breadcrumbs.setAdapter(new BreadcrumbAdapter(this.parentCollections == null ? Collections.emptyList() : this.parentCollections));

        this.mediaGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        this.mediaGridLayoutManager.setItemPrefetchEnabled(true);
        this.mediaGrid = findViewById(R.id.media_fragment_photo_gridview);
        this.mediaGrid.setLayoutManager(this.mediaGridLayoutManager);
        this.mediaGridAdapter = new PhotoGridAdapter(new ArrayList<>());
        this.mediaGrid.setAdapter(this.mediaGridAdapter);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        });

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                RecyclerView.LayoutManager layoutManager = mediaGrid.getLayoutManager();
                if(!names.isEmpty() && names.get(0) != null && layoutManager != null) {
                    String transitionName = names.get(0);
                    for(int i = 0; i < mediaGridAdapter.getItemCount(); i++) {
                        View view = layoutManager.findViewByPosition(i);
                        if(view != null) {
                            View imageView = view.findViewById(R.id.media_grid_photo);
                            if(imageView != null && transitionName.equals(imageView.getTransitionName())) {
                                sharedElements.put(transitionName, imageView);
                                startPostponedEnterTransition();
                                return;
                            }
                        }
                    }
                }
            }
        });

        Services.getInstance().mediaService.photos(this.collectionID, 0, collection == null ? Integer.MAX_VALUE : collection.numOfPhotos).enqueue(new Callback<Pageable<MediaFileMetaData>>() {
            @Override
            public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
                Pageable<MediaFileMetaData> r = response.body();
                if(r != null) {
                    MediaActivity.this.mediaGridAdapter.addItems(r.getContent());
                }
            }
        });
    }

    private View getViewAt(int position) {
        RecyclerView.LayoutManager manager = mediaGrid.getLayoutManager();
        View containerView = null;
        if(manager != null) {
            containerView = manager.findViewByPosition(position);
        }
        if(containerView != null) {
            return containerView.findViewById(R.id.media_grid_photo);
        }
        return null;
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        resultCode--;
        if(getViewAt(resultCode) == null) {
            postponeEnterTransition();
            mediaGrid.getViewTreeObserver().addOnGlobalLayoutListener(startPostponedIfEverythingElseFails);
        }
        this.mediaGridLayoutManager.scrollToPosition(resultCode);
        super.onActivityReenter(resultCode, data);
    }

    private ViewTreeObserver.OnGlobalLayoutListener startPostponedIfEverythingElseFails = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Force the layout to continue in the rare case the view was null, but the sharedElementsMap was not updated
            mediaGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            Log.v("MediaActivity", "Starting postponed enter transition as backup, because the view has been updated");
            startPostponedEnterTransition();
        }
    };

    class PhotoGridAdapter extends BaseItemAdapter<PhotoGridAdapter.ViewHolder, MediaFileMetaData> {

        protected PhotoGridAdapter(List<MediaFileMetaData> items) {
            super(items);
        }

        @Override
        public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_photo_grid_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindCustomViewHolder(ViewHolder holder, int position) {
            Drawable placeholder = getDrawable(R.drawable.animated_uscki_logo_white);
            if(placeholder instanceof Animatable) {
                ((Animatable)placeholder).start();
            }
            holder.photoData = this.items.get(position);
            holder.photoView.setTransitionName("media_browser_" + position);
            Glide.with(MediaActivity.this)
                    .load(MediaAPI.getMediaUrl(holder.photoData.getId(), MediaAPI.MediaSize.NORMAL))
                    .fitCenter()
                    .thumbnail(Glide.with(MediaActivity.this).load(MediaAPI.getMediaUri(holder.photoData.getId(), MediaAPI.MediaSize.SMALL)))
                    .placeholder(placeholder)
                    .error(R.drawable.ic_wilson)
                    .into(holder.photoView);
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            MediaFileMetaData photoData;
            ImageView photoView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                photoView = itemView.findViewById(R.id.media_grid_photo);
                itemView.setOnClickListener(this);
            }

            public void onClick(View v) {
                Intent intent = new FullScreenMediaActivity
                        .CollectionIntentBuilder(collection.name, "media_browser_")
                        .collectionID(collectionID)
                        .initialPosition(getItemPosition(photoData.getId()), items)
                        .build(MediaActivity.this);

                MediaActivity.this.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(MediaActivity.this, photoView, photoView.getTransitionName()).toBundle());
            }
        }
    }

    static class BreadcrumbAdapter extends RecyclerView.Adapter<BreadcrumbAdapter.ViewHolder> {

        private List<MediaCollection> mediaCollectionList;

        public BreadcrumbAdapter(List<MediaCollection> mediaCollectionList) {
            this.mediaCollectionList = mediaCollectionList;
        }

        public void addItems(List<MediaCollection> mediaCollections) {
            this.mediaCollectionList.addAll(mediaCollections);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.breadcrumb_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.crumbImage.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            if(position == getItemCount() - 1) {
                holder.crumbTitle.setTypeface(null, Typeface.BOLD);
            }
            holder.crumbTitle.setText(this.mediaCollectionList.get(position).name);
            holder.itemView.setOnClickListener(v -> {
                // TODO find a way to do this, preferably just triggering onBackPressed a few times
                Log.e("MediaActivity", "Clicked to go back to position " + position);
            });
        }

        @Override
        public int getItemCount() {
            return this.mediaCollectionList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView crumbImage;
            private TextView crumbTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.crumbImage = itemView.findViewById(R.id.crumb_image);
                this.crumbTitle = itemView.findViewById(R.id.crumb_title);
            }
        }
    }

    static class IntentBuilder {
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
            Intent intent = new Intent(context, MediaActivity.class);
            intent.putExtra(MediaOverviewFragment.ARG_COLLECTION_ID, this.collectionID);
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
                intent.putParcelableArrayListExtra(MediaOverviewFragment.ARG_COLLECTION_PARENTS, parents);
            }
            return intent;
        }
    }
}