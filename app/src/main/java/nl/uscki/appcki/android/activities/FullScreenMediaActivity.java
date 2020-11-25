package nl.uscki.appcki.android.activities;

import android.Manifest;
import android.app.SharedElementCallback;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.igreenwood.loupe.Loupe;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;
import nl.uscki.appcki.android.generated.media.MediaTag;
import retrofit2.Response;

public class FullScreenMediaActivity extends BasicActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 33847;

    public static final String ARGS_TITLE = "TITLE";
    public static final String ARGS_TRANSITION_NAME_TEMPLATE = "TRANSITION_NAME_TEMPLATE";
    public static final String ARGS_IMAGES_COLLECTION_ID = "IMAGE_COLLECTION";

    public static final String ARGS_INITIAL_IMAGE_METADATA = "INITIAL_IMAGE_METADATA";
    public static final String ARGS_IMAGE_SINGLE_ID = "SINGLE_IMAGE_MEDIA_ID";
    public static final String ARGS_IMAGE_URL = "SINGLE_IMAGE_URL";
    public static final String ARGS_INITIAL_POSITION = "INITIAL_POSITION";
    public static final String ARGS_COLLECTION_IS_SMOBO = "COLLECTION_IS_SMOBO";
    public static final String ARGS_COLLECTION_SIZE = "COLLECTION_SIZE";

    public static final String ARGS_PREVIOUS_IMAGE_METADATA = "PREVIOUS_IMAGE_METADATA";
    public static final String ARGS_NEXT_IMAGE_METADATA = "NEXT_IMAGE_METADATA";


    private String title;
    private String transitionNameTemplate;
    private int collectionId;

    private MediaFileMetaData initialImage;
    private int singleImageMediaId = -1;
    private String singleImageUrl;
    private MediaFileMetaData previousImage;
    private MediaFileMetaData nextImage;

    private int initialPosition;
    private int size;
    private MediaItemAdapter adapter = null;
    private Toolbar toolbar;

    private boolean isSmobo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("FullScreenMediaActivity", "Got intent, starting...");
        this.transitionNameTemplate = getIntent().getStringExtra(ARGS_TRANSITION_NAME_TEMPLATE);

        this.collectionId = getIntent().getIntExtra(ARGS_IMAGES_COLLECTION_ID, -1);
        this.title = getIntent().getStringExtra(ARGS_TITLE);

        this.initialImage = getIntent().getParcelableExtra(ARGS_INITIAL_IMAGE_METADATA);
        this.previousImage = getIntent().getParcelableExtra(ARGS_PREVIOUS_IMAGE_METADATA);
        this.nextImage = getIntent().getParcelableExtra(ARGS_NEXT_IMAGE_METADATA);

        this.singleImageMediaId = this.initialImage == null ? getIntent().getIntExtra(ARGS_IMAGE_SINGLE_ID, -1) : this.initialImage.getId();
        this.singleImageUrl = getIntent().getStringExtra(ARGS_IMAGE_URL);

        this.initialPosition = getIntent().getIntExtra(ARGS_INITIAL_POSITION, 0);
        this.size = getIntent().getIntExtra(ARGS_COLLECTION_SIZE, -1);
        this.isSmobo = getIntent().getBooleanExtra(ARGS_COLLECTION_IS_SMOBO, false);

        postponeEnterTransition();

        if(this.size < 0) {
            setContentView(R.layout.full_screen_media_view);
            initSingleImageView();
        } else {
            setContentView(R.layout.full_screen_media_viewpager);
            initViewPager();
        }

        initToolbar();
    }

    String getCurrentImageLink() {
        return getImageLink(this.collectionId, this.adapter.getImageAtCurrentPosition().getId());
    }

    static String getImageLink(int collectionId, int mediaFileId) {
        return String.format(Locale.getDefault(), "https://www.uscki.nl/?pagina=Media/Archive&subcollection=%d&mediafile=%d", collectionId, mediaFileId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.fullscreen_image_menu, menu);

        menu.findItem(R.id.fullscreen_image_menu_download_button).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                writeMediaIfPermitted();
                return true;
            }
        });
        menu.findItem(R.id.fullscreen_image_menu_share_image).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Glide.with(FullScreenMediaActivity.this)
                        .downloadOnly()
                        .load(MediaAPI.getMediaUri(adapter.getImageAtCurrentPosition().getId(), MediaAPI.MediaSize.LARGE))
                        .into(shareMediaCallback);
                return true;
            }
        });

       menu.findItem(R.id.fullscreen_image_menu_share_link).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               Intent shareIntent = createBasicMediaShareIntent();
               shareIntent.putExtra(Intent.EXTRA_TEXT, getCurrentImageLink());
               shareIntent.setType("text/*");
               startActivity(Intent.createChooser(shareIntent, "Send to..."));
               return false;
           }
       });

       if(this.initialImage != null && this.initialImage.getCollection() != null ||
        this.collectionId == -1 && this.adapter != null
       ) {
           MenuItem goToCollection = menu.findItem(R.id.fullscreen_image_menu_show_collection);
           goToCollection.setVisible(true);
           goToCollection.setOnMenuItemClickListener((v) -> {
               MediaCollection collection = null;
               if(this.initialImage != null) {
                   collection = this.initialImage.getCollection();
               } else if (this.adapter != null && this.adapter.getImageAtCurrentPosition() != null) {
                   collection = this.adapter.getImageAtCurrentPosition().getCollection();
               }
               if(collection == null) {
                   Toast.makeText(FullScreenMediaActivity.this, "Geen collectie gevonden voor deze afbeelding", Toast.LENGTH_LONG).show(); // TODO string resource
                   return false;
               }
               Intent intent = new MediaActivity.IntentBuilder(collection)
//                       .setDirectParent(this.singleImage.getCollection().getParent()) // this is not going to help much, since parent is always null;
                       .build(this);
               startActivity(intent);
              return false;
           });
       }

       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        goBackToPhotoOverview();
        if(adapter != null) {
            this.adapter.clear();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        this.adapter = null;
        super.onDestroy();
    }

    private Intent createBasicMediaShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TITLE, "Foto uit <collectienaam>"); // TODO collection name // TODO string resource
        intent.putExtra(Intent.EXTRA_SUBJECT, "<Insert media collection name here>"); // TODO
        intent.putExtra(Intent.EXTRA_TEXT, getCurrentImageLink());
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private void writeMediaIfPermitted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(FullScreenMediaActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
            } else {
                Glide.with(FullScreenMediaActivity.this)
                        .asBitmap()
                        .load(MediaAPI.getMediaUri(adapter.getImageAtCurrentPosition().getId(), MediaAPI.MediaSize.LARGE))
                        .into(storeMediaCallback);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeMediaIfPermitted();
            } else {
                Toast.makeText(
                        this,
                        "Afbeelding kan niet worden opgeslagen zonder toestemming voor storage", // TODO make string resource
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    CustomTarget<Bitmap> storeMediaCallback = new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_external_media_subdir); // TODO seperate by collection?
            Log.e("StoreImage", "Relative output dir is " + relativeLocation);

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, adapter.getImageAtCurrentPosition().getCollection().name.trim().replaceAll(" ", "_") + "_" + System.currentTimeMillis()); // TODO
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg"); // TODO make general
            values.put(MediaStore.MediaColumns.IS_DOWNLOAD, 1);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.OWNER_PACKAGE_NAME, getPackageName());
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
                values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            }

            ContentResolver resolver = FullScreenMediaActivity.this.getContentResolver();
            Uri output = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                if(output == null) throw new IOException("Failed to create a MediaStore record");
                OutputStream os = resolver.openOutputStream(output);
                if (os == null) throw new IOException("Failed to create the output stream");

                if (!resource.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                    throw new IOException("Failed to save the bitmap");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(output, values, null, null);
                }
                Log.e("StoreImage", "Image saved in " + output);
            Toast.makeText(FullScreenMediaActivity.this, String.format(Locale.getDefault(), "Abeelding opgeslagen in %s%s%s", Environment.DIRECTORY_PICTURES, File.separator, getString(R.string.app_external_media_subdir)), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                if(output != null) {
                    resolver.delete(output, null, null);
                }
           Toast.makeText(FullScreenMediaActivity.this, "Opslaan van afbeelding mislukt", Toast.LENGTH_LONG).show(); // TODO string resource
            }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };

    private CustomTarget<File> shareMediaCallback = new CustomTarget<File>() {
        @Override
        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
            String[] mimeTypeArray = new String[] { "image/jpeg"}; // TODO extract mime type
            Intent intent = createBasicMediaShareIntent();
            intent.setType("image/jpeg");

            Uri uri = FileProvider.getUriForFile(FullScreenMediaActivity.this, getPackageName() + ".provider", resource);;

            intent.setClipData(new ClipData(
                    "Foto van U.S.C.K.I. Incognito uit de media collectie \"collectienaam\" (" + getCurrentImageLink() + ")", // TODO
                    mimeTypeArray,
                    new ClipData.Item(uri)
            ));

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Intent title")); // TODO
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }
    };

    private RecyclerView tagView;
    private TextView indexTextView;
    private TextView dateAddedTextView;
    private void initSingleImageView() {
        ImageView imageView = findViewById(R.id.image);
        if(this.singleImageMediaId < 0) {
            Glide.with(this)
                    .load(this.singleImageUrl)
                    .listener(simpleGlideRequestListener)
                    .error(R.drawable.ic_wilson)
                    .into(imageView);
            findViewById(R.id.full_screen_image_helpers_container).setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(MediaAPI.getMediaUri(this.singleImageMediaId, MediaAPI.MediaSize.LARGE))
                    .thumbnail(Glide.with(this).load(MediaAPI.getMediaUri(this.singleImageMediaId, MediaAPI.MediaSize.SMALL)).listener(simpleGlideRequestListener)
                            .error(R.drawable.ic_wilson))
                    .listener(simpleGlideRequestListener)
                    .error(R.drawable.ic_wilson)
                    .into(imageView);

            if(this.initialImage == null) {
                findViewById(R.id.full_screen_image_helpers_container).setVisibility(View.GONE);
            } else {
                this.tagView = findViewById(R.id.full_screen_image_tags);
                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(FullScreenMediaActivity.this);
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setJustifyContent(JustifyContent.CENTER);
                this.tagView.setLayoutManager(layoutManager);
                this.tagView.setAdapter(new ImageTagAdapter(this.initialImage.getTags()));

                if (this.title == null && this.initialImage.getCollection() != null)
                    this.title = this.initialImage.getCollection().getName();

                this.indexTextView = findViewById(R.id.full_screen_image_current_index);
                this.indexTextView.setVisibility(View.GONE);
                this.dateAddedTextView = findViewById(R.id.full_screen_image_date_added);
                if (this.initialImage.getAdded() != null) {
                    this.dateAddedTextView.setVisibility(View.VISIBLE);
                    this.dateAddedTextView.setText("Toegevoegd op " + this.initialImage.getAdded().toString("D MM Y"));
                }
            }
        }
    }

    private RequestListener<Drawable> simpleGlideRequestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            startPostponedEnterTransition();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            ImageView image = findViewById(R.id.image);
            ViewGroup container = findViewById(R.id.container);
            image.setTransitionName(transitionNameTemplate); // TODO make transition name variable
            Loupe loupe = new Loupe(image, container);
            loupe.setUseFlingToDismissGesture(false);
            loupe.setOnScaleChangedListener((v, v1, v2) -> {
                if(v > 1.4) changeUiVisbility(false, indexTextView, dateAddedTextView, tagView); else changeUiVisbility(true, indexTextView, dateAddedTextView, tagView);
            });
            loupe.setOnViewTranslateListener(new Loupe.OnViewTranslateListener() {
                @Override
                public void onStart(@NotNull ImageView imageView) {
                    changeUiVisbility(false, indexTextView, dateAddedTextView, tagView);
                }

                @Override
                public void onViewTranslate(@NotNull ImageView imageView, float v) {
                }

                @Override
                public void onDismiss(@NotNull ImageView imageView) {
                    goBackToPhotoOverview();
                }

                @Override
                public void onRestore(@NotNull ImageView imageView) {
                    changeUiVisbility(true, indexTextView, dateAddedTextView, tagView);
                }
            });

            startPostponedEnterTransition();
            return false;
        }
    };

    private void initViewPager() {
        this.adapter = new MediaItemAdapter(this, collectionId, initialPosition, size,
                initialImage, previousImage, nextImage);
        ViewPager viewPager = findViewById(R.id.fullscreen_media_viewpager);
        viewPager.setAdapter(this.adapter);
        viewPager.setCurrentItem(this.initialPosition);
    }

    private void initToolbar() {
        this.toolbar = (this.size >= 0) ? findViewById(R.id.fullscreen_media_toolbar) : findViewById(R.id.fullscreen_media_single_image_toolbar);
        if(this.toolbar != null) {
            this.toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(this.toolbar);
        }
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(this.title);
        }
    }

    private void hideToolbar() {
        if(this.toolbar != null) {
            this.toolbar.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0f - toolbar.getHeight());
        }
    }

    private void showToolbar() {
        if(this.toolbar != null) {
            this.toolbar.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0f);
        }
    }

    private void changeUiVisbility(boolean visible, TextView indexTextView, TextView dateAddedTextView, RecyclerView tagView) {
        if(visible) {
            if(indexTextView != null)
                indexTextView.animate().translationX(0).setDuration(500);
            if(tagView != null)
                tagView.animate().translationY(0).setDuration(500);
            if(dateAddedTextView != null)
                dateAddedTextView.animate().translationX(0).setDuration(500);
            showToolbar();
        } else {
            if(indexTextView != null)
                indexTextView.animate().translationX(getWindow().getDecorView().getWidth() - indexTextView.getLeft()).setDuration(500);
            if(tagView != null)
                tagView.animate().translationY(getWindow().getDecorView().getHeight() - tagView.getTop()).setDuration(500);
            if(dateAddedTextView != null)
                dateAddedTextView.animate().translationX(-1 * dateAddedTextView.getRight()).setDuration(500);
            hideToolbar();
        }
        this.helpersVisible = visible;
    }

    private void goBackToPhotoOverview() {
        if(this.adapter != null)
            // Result code needs to be non-zero positive value for onActivityReenter to be called.
            setResult(adapter.getCurrentPosition() + 1);
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackToPhotoOverview();
        return true;
    }

    boolean helpersVisible = true;

    class MediaItemAdapter extends PagerAdapter {
        private Context context;
        private MediaFileMetaData[] media;
        private final int size;

        private Map<Integer, Loupe> loupeMap = new HashMap<>();
        private Map<Integer, ImageView> viewMap = new HashMap<>();
        private int currentPosition;

        public MediaItemAdapter(Context context, int collectionId, int initialPosition, int totalImages,
                                MediaFileMetaData thisImage, MediaFileMetaData previousImage, MediaFileMetaData nextImage) {
            this.size = totalImages;
            this.context = context;
            this.media = new MediaFileMetaData[totalImages];
            if(thisImage != null) {
                this.media[initialPosition] = thisImage;
            }
            if(previousImage != null && initialPosition > 0) {
                this.media[initialPosition - 1] = previousImage;
            }
            if(nextImage != null && initialPosition < totalImages - 1) {
                this.media[initialPosition + 1] = nextImage;
            }
            if(isSmobo) {
                Services.getInstance().smoboService.photos(collectionId, 0, size).enqueue(fullCollectionCallback);
            } else {
                Services.getInstance().mediaService.photos(collectionId, 0, size).enqueue(fullCollectionCallback);
            }
        }

        private Callback<Pageable<MediaFileMetaData>> fullCollectionCallback = new Callback<Pageable<MediaFileMetaData>>() {
            @Override
            public void onSucces(Response<Pageable<MediaFileMetaData>> response) {
                if(response.body() != null) {
                    List<MediaFileMetaData> metaDataList = response.body().getContent();
                    for(int i = 0; i < metaDataList.size(); i++) {
                        media[i] = metaDataList.get(i);
                    }
                }
            }
        };

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewGroup imageContainer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.full_screen_media_view, container, false);
            container.addView(imageContainer);
            ImageView image = imageContainer.findViewById(R.id.image);

            RecyclerView tagRecycler = imageContainer.findViewById(R.id.full_screen_image_tags);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(FullScreenMediaActivity.this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.CENTER);
            tagRecycler.setLayoutManager(layoutManager);
            TextView dateAddedTextView = imageContainer.findViewById(R.id.full_screen_image_date_added);

            if(media[position] != null) {
                tagRecycler.setAdapter(new ImageTagAdapter(media[position].getTags()));
                dateAddedTextView.setText("Toegevoegd op " + media[position].getCollection().getDate().toString("d MMMM Y")); // TODO string resources
            }

            TextView currentIndexTextView = imageContainer.findViewById(R.id.full_screen_image_current_index);
            currentIndexTextView.setText(String.format(Locale.getDefault(), "%d / %d", position + 1, getCount()));

            if(isSmobo) {
                dateAddedTextView.setVisibility(View.VISIBLE);
            }

            View helpersContainer = imageContainer.findViewById(R.id.full_screen_image_helpers_container);
            helpersContainer.setOnClickListener(v ->
                    changeUiVisbility(!helpersVisible, currentIndexTextView, dateAddedTextView, tagRecycler));

            loadImage(image, imageContainer, position, currentIndexTextView, dateAddedTextView, tagRecycler);
            viewMap.put(position, image);
            return imageContainer;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            this.currentPosition = position;
            showToolbar();
            trySetTitle(media[position]);
        }

        private void trySetTitle(MediaFileMetaData metadata) {
            if(toolbar != null) {
                if(metadata != null && metadata.getCollection() != null) {
                    toolbar.setTitle(metadata.getCollection().getName());
                } else {
                    toolbar.setTitle("");
                }
            }
        }

        public MediaFileMetaData getImageAtCurrentPosition() {
            return media[this.currentPosition];
        }

        @Override
        public int getCount() {
            return this.size;
        }

        private void loadImage(final ImageView image, final ViewGroup container, final int position, TextView indexText, TextView dateText, RecyclerView tagView) {
            if(media[position] == null) return;

            String thumbnailURL = MediaAPI.getMediaUrl(this.media[position].getId(), MediaAPI.MediaSize.SMALL);
            String imageUrl = MediaAPI.getMediaUrl(this.media[position].getId(), MediaAPI.MediaSize.LARGE);

            RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (isFirstResource) startPostponedEnterTransition();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (isFirstResource)
                        performResourceReadyActions(image, container, position, indexText, dateText, tagView);
                    return false;
                }
            };

            Drawable loadingAnimation = getDrawable(R.drawable.animated_uscki_logo_white);
            if (loadingAnimation instanceof Animatable)
                ((Animatable) loadingAnimation).start();
            Glide.with(FullScreenMediaActivity.this)
                    .load(imageUrl)
                    .thumbnail(
                            Glide.with(FullScreenMediaActivity.this)
                                .load(thumbnailURL)
                                .error(R.drawable.ic_wilson)
                                .listener(requestListener)
                    )
                    .listener(requestListener)
                    .error(R.drawable.ic_wilson)
                    .into(image);
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        void clear() {
            for(Loupe loupe : this.loupeMap.values()) {
                loupe.cleanup();
            }
            this.loupeMap.clear();
        }

        private void performResourceReadyActions(ImageView image, ViewGroup container, int position, TextView indexText, TextView dateText, RecyclerView tagView) {
            image.setTransitionName(transitionNameTemplate + position);
            Loupe loupe = new Loupe(image, container);
            loupe.setUseFlingToDismissGesture(false);
            loupe.setOnScaleChangedListener((v, v1, v2) -> {
                if(v > 1.4) changeUiVisbility(false, indexText, dateText, tagView); else changeUiVisbility(true, indexText, dateText, tagView);
            });
            loupe.setOnViewTranslateListener(new Loupe.OnViewTranslateListener() {
                @Override
                public void onStart(@NotNull ImageView imageView) {
                    changeUiVisbility(false, indexText, dateText, tagView);
                }

                @Override
                public void onViewTranslate(@NotNull ImageView imageView, float v) {
                }

                @Override
                public void onDismiss(@NotNull ImageView imageView) {
                    goBackToPhotoOverview();
                }

                @Override
                public void onRestore(@NotNull ImageView imageView) {
                    changeUiVisbility(true, indexText, dateText, tagView);
                }
            });
            loupeMap.put(position, loupe);

            if (position == initialPosition) {
                setEnterSharedElementCallback(new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        if(names == null) return;

                        ViewPager viewPager = findViewById(R.id.fullscreen_media_viewpager);
                        if(viewPager == null || !viewMap.containsKey(viewPager.getCurrentItem()))
                            return;

                        View view = viewMap.get(viewPager.getCurrentItem());
                        viewPager.setTransitionName(transitionNameTemplate + currentPosition);
                        if(sharedElements != null) {
                            sharedElements.clear();
                            sharedElements.put(viewPager.getTransitionName(), view);
                        }
                    }
                });
                startPostponedEnterTransition();
            }
        }
    }

    class ImageTagAdapter extends RecyclerView.Adapter<ImageTagAdapter.ViewHolder> {

        private List<MediaTag> tags;

        public ImageTagAdapter(List<MediaTag> tags) {
            this.tags = tags;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FullScreenMediaActivity.this).inflate(R.layout.image_tag, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.username.setText(tags.get(position).getName());
            holder.itemView.setOnLongClickListener(v -> false);
            holder.itemView.setOnClickListener(v -> Log.v("MediaTag", "Tag clicked: Implement media view for tags! " + tags.get(position).getName()));
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView username;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.username = itemView.findViewById(R.id.image_tag_username);
            }
        }
    }

    private static class IntentBuilder {
        private String title;
        private String transitionTemplate;

        private IntentBuilder(String title, String transitionnameTemplate) {
            this.title = title;
            this.transitionTemplate = transitionnameTemplate;
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, FullScreenMediaActivity.class);
            intent.putExtra(FullScreenMediaActivity.ARGS_TITLE, this.title);
            intent.putExtra(FullScreenMediaActivity.ARGS_TRANSITION_NAME_TEMPLATE, transitionTemplate);
            return intent;
        }
    }

    public static class CollectionIntentBuilder extends IntentBuilder {
        private ArrayList<MediaFileMetaData> items;
        private int initialPosition;
        private int collectionID;
        private boolean isSmobo = false;
        private int size;
        private MediaFileMetaData initialImage;
        private MediaFileMetaData previousImage;
        private MediaFileMetaData nextImage;

        public CollectionIntentBuilder(String title, String transitionnameTemplate) {
            super(title, transitionnameTemplate);
        }

        public CollectionIntentBuilder collectionID(int collectionID) {
            this.collectionID = collectionID;
            return this;
        }

        public CollectionIntentBuilder isSmobo() {
            this.isSmobo = true;
            return this;
        }

        public CollectionIntentBuilder initialPosition(int initialPosition, List<MediaFileMetaData> media) {
            this.initialPosition = initialPosition;
            this.initialImage = media.get(initialPosition);
            this.size = media.size();
            if(initialPosition > 0) {
                this.previousImage = media.get(initialPosition - 1);
            }
            if(initialPosition < media.size()-1) {
                this.nextImage = media.get(initialPosition+1);
            }
            return this;
        }

        @Override
        public Intent build(Context context) {
            Intent intent = super.build(context);
            intent.putExtra(FullScreenMediaActivity.ARGS_IMAGES_COLLECTION_ID, this.collectionID);
            intent.putExtra(FullScreenMediaActivity.ARGS_INITIAL_POSITION, this.initialPosition);
            intent.putExtra(FullScreenMediaActivity.ARGS_COLLECTION_SIZE, this.size);
            intent.putExtra(FullScreenMediaActivity.ARGS_INITIAL_IMAGE_METADATA, (Parcelable) this.initialImage);
            intent.putExtra(FullScreenMediaActivity.ARGS_PREVIOUS_IMAGE_METADATA, (Parcelable) this.previousImage);
            intent.putExtra(FullScreenMediaActivity.ARGS_NEXT_IMAGE_METADATA, (Parcelable) this.nextImage);
            intent.putExtra(ARGS_COLLECTION_IS_SMOBO, this.isSmobo);
            return intent;
        }
    }

    public static class SingleImageIntentBuilder extends IntentBuilder {
        private String url;
        private int mediaId = -1;
        private MediaFileMetaData media;

        public SingleImageIntentBuilder(String title, String transitionnameTemplate) {
            super(title, transitionnameTemplate);
        }

        public SingleImageIntentBuilder url(String url) {
            this.url = url;
            return this;
        }

        public SingleImageIntentBuilder media(int mediaId) {
            this.mediaId = mediaId;
            return this;
        }

        public SingleImageIntentBuilder media(MediaFileMetaData mediaFile) {
            this.media = mediaFile;
            return this;
        }

        @Override
        public Intent build(Context context) {
            Intent intent = super.build(context);
            if(this.media != null) {
                intent.putExtra(FullScreenMediaActivity.ARGS_INITIAL_IMAGE_METADATA, (Parcelable) this.media);
            } else if(this.mediaId >= 0) {
                intent.putExtra(FullScreenMediaActivity.ARGS_IMAGE_SINGLE_ID, this.mediaId);
            } else {
                intent.putExtra(FullScreenMediaActivity.ARGS_IMAGE_URL, this.url);
            }
            return intent;
        }
    }
}