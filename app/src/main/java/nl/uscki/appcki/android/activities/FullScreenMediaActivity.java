package nl.uscki.appcki.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.media.helpers.MediaActionHelper;
import nl.uscki.appcki.android.fragments.media.helpers.view.FullScreenMediaView;
import nl.uscki.appcki.android.fragments.media.helpers.view.collection.FullScreenPagerCollectionView;
import nl.uscki.appcki.android.fragments.media.helpers.view.collection.FullScreenPagerSmoboView;
import nl.uscki.appcki.android.fragments.media.helpers.view.single.FullScreenMediaSingleMediaUrlView;
import nl.uscki.appcki.android.fragments.media.helpers.view.single.FullScreenMediaSingleMediaUsckiView;
import nl.uscki.appcki.android.generated.media.MediaCollection;
import nl.uscki.appcki.android.generated.media.MediaFileMetaData;

public class FullScreenMediaActivity extends BasicActivity {

    public static final int REQUEST_STORAGE_PERMISSION = 33847;

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

    private FullScreenMediaView mediaView;
    private MediaActionHelper actionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();

        Intent intent = getIntent();
        int collectionSize = intent.getIntExtra(ARGS_COLLECTION_SIZE, -1);
        if(collectionSize < 0) {
            if(intent.hasExtra(ARGS_IMAGE_URL)) {
                this.mediaView = new FullScreenMediaSingleMediaUrlView(this, intent);
            } else {
                this.mediaView = new FullScreenMediaSingleMediaUsckiView(this, intent);
            }
        } else {
            if(intent.getBooleanExtra(ARGS_COLLECTION_IS_SMOBO, false)) {
                this.mediaView = new FullScreenPagerSmoboView(this, intent);
            } else {
                this.mediaView = new FullScreenPagerCollectionView(this, intent);
            }
        }

        this.mediaView.init();
        this.actionHelper = new MediaActionHelper(this, this.mediaView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.fullscreen_image_menu, menu);

        menu.findItem(R.id.fullscreen_image_menu_download_button).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                actionHelper.writeMediaIfPermitted();
                return true;
            }
        });
        menu.findItem(R.id.fullscreen_image_menu_share_image).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                actionHelper.shareMedia();
                return true;
            }
        });

       menu.findItem(R.id.fullscreen_image_menu_share_link).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
                actionHelper.shareUrl();
               return false;
           }
       });

       if(this.mediaView.canNavigateCollection()) {
           MenuItem goToCollection = menu.findItem(R.id.fullscreen_image_menu_show_collection);
           goToCollection.setVisible(true);
           goToCollection.setOnMenuItemClickListener((v) -> {
               MediaCollection collection = this.mediaView.getCurrentImage().hasCollection() ?
                       this.mediaView.getCurrentImage().getMetaData().getCollection() : null;
               if(collection == null) {
                   Toast.makeText(FullScreenMediaActivity.this, "Geen collectie gevonden voor deze afbeelding", Toast.LENGTH_LONG).show(); // TODO string resource
                   return false;
               }
               Intent intent = new MediaActivity.IntentBuilder(collection)
                       .setDirectParent(collection.getParent()) // this is not going to help much, since parent is always null;
                       .build(this);
               startActivity(intent);
              return false;
           });
       }

       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.actionHelper.writeMediaIfPermitted();
            } else {
                Toast.makeText(
                        this,
                        "Afbeelding kan niet worden opgeslagen zonder toestemming voor storage", // TODO make string resource
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        goBackToPhotoOverview();
        this.mediaView.cleanup();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        this.mediaView.destroy();
        super.onDestroy();
    }

    public void goBackToPhotoOverview() {
        // Result code needs to be non-zero positive value for onActivityReenter to be called.
        setResult(this.mediaView.getCurrentAdapterPosition() + 1);
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackToPhotoOverview();
        return true;
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