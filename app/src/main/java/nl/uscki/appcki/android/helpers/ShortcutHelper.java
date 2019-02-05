package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.util.Log;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.shop.StoreFragment;
import nl.uscki.appcki.android.generated.shop.Store;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A helper class to construct pinned shortcuts
 */
public class ShortcutHelper {

    private Context context;

    /**
     * Constructor
     * @param context   A context reference
     */
    public ShortcutHelper(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Try to create a pinned shortcut for a shop. This process may fail if the platform does
     * not support pinned shortcuts
     *
     * @param store     Store for which to create shortcut
     */
    public void createShortcutForShop(Store store) {
        Intent openShopIntent = new Intent(context, MainActivity.class);
        openShopIntent.setAction(MainActivity.ACTION_VIEW_STORE);
        openShopIntent.putExtra(StoreFragment.PARAM_STORE_ID, store.getId());

        new Builder("shop-shortcut-9-" + store.getId(), store.title, openShopIntent)
                .setLongLabel(context.getString(R.string.shop_long_store_name, store.title))
                .setMediaID(store.image, R.drawable.cash_multiple)
                .build();
    }

    public class Builder {
        private String shortcutID;
        private String shortLabel;
        private String longLabel;
        private IconCompat icon;
        private int mediaID = -1; // for background
        private Bitmap backupBitmap;
        private int backupResourceID = -1;
        private IconCompat backupIcon;
        private Bitmap bitmap;
        private int resourceID = -1;
        private Intent intent;
        private IntentSender callback; // Callback when Icon is created

        /**
         * Construct a new ShortcutHelper.Builder object
         *
         * @param shortcutID    The shortcut ID for the pinned shortcut. This ID is rigid and
         *                      can be used to change the shortcut later on. However, creating a
         *                      new shortcut with an existing ID takes the parameters of the
         *                      previous shortcut, instead of the new one.
         * @param shortLabel    The short label for this shortcut. This label will be shown on some
         *                      launchers where space is limited (e.g. the home screen)
         * @param intent        The intent for when this shortcut is clicked.
         */
        public Builder(String shortcutID, String shortLabel, Intent intent) {
            this.shortcutID = shortcutID;
            this.shortLabel = shortLabel;
            this.intent = intent;
        }

        /**
         * Set the shortcut ID for the pinned shortcut. This ID is rigid and can be used to change
         * the shortcut later on. However, creating a new shortcut with an existing ID takes the
         * parameters of the previous shortcut, instead of the new one.
         *
         * {@see https://developer.android.com/reference/android/support/v4/content/pm/ShortcutInfoCompat.Builder.html#ShortcutInfoCompat.Builder(android.content.Context,%20java.lang.String)}
         *
         * @param shortcutID    Unique shortcut ID
         * @return              This builder object
         */
        public Builder setShortcutID(String shortcutID) {
            this.shortcutID = shortcutID;
            return this;
        }

        /**
         * Set the short label for this shortcut. This label will be shown on some launchers where
         * space is limited (e.g. the home screen)
         *
         * {@see https://developer.android.com/reference/android/support/v4/content/pm/ShortcutInfoCompat.Builder.html#setShortLabel(java.lang.CharSequence)}
         *
         * @param shortLabel    Short label for this shortcut
         * @return              This builder object
         */
        public Builder setShortLabel(String shortLabel) {
            this.shortLabel = shortLabel;
            return this;
        }

        /**
         * Set the long label for this shortcut. This label will be shown on some launchers where
         * there is enough space, e.g. in the shortcut list on long-pressing the app icon
         *
         * {@see https://developer.android.com/reference/android/support/v4/content/pm/ShortcutInfoCompat.Builder.html#setLongLabel(java.lang.CharSequence)}
         *
         * @param longLabel     Long label for this shortcut
         * @return              This builder object
         */
        public Builder setLongLabel(String longLabel) {
            this.longLabel = longLabel;
            return this;
        }

        /**
         * Set the icon for this shortcut. This icon will be used as the background for the shortcut
         *
         * This field is ignored of a media ID is already specified.
         *
         * {@see https://developer.android.com/reference/android/support/v4/content/pm/ShortcutInfoCompat.Builder.html#setIcon(android.support.v4.graphics.drawable.IconCompat)}
         *
         * @param icon          Icon for this shortcut
         * @return              This builder object
         */
        public Builder setIcon(IconCompat icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Use a remote image file from the B.A.D.W.O.L.F. API as the icon for this shortcut. This
         * request may fail, so a backup resource is required.
         *
         * If a media ID is specified, this always takes precedent over a specified Icon, bitmap or
         * drawable.
         *
         * @param mediaID           Media ID for remote image file
         * @param backupResourceID  Resource ID of drawable resource to use if this request fails
         * @return                  This builder object
         */
        public Builder setMediaID(int mediaID, int backupResourceID) {
            this.mediaID = mediaID;
            this.backupResourceID = backupResourceID;
            return this;
        }

        /**
         * Use a remote image file from the B.A.D.W.O.L.F. API as the icon for this shortcut. This
         * request may fail, so a backup resource is required.
         *
         * If a media ID is specified, this always takes precedent over a specified Icon, bitmap or
         * drawable.
         *
         * @param mediaID           Media ID for remote image file
         * @param backupBitmap      Bitmap to use of this request fails
         * @return                  This builder object
         */
        public Builder setMediaID(int mediaID, Bitmap backupBitmap) {
            this.mediaID = mediaID;
            this.backupBitmap = backupBitmap;
            return this;
        }

        /**
         * Use a remote image file from the B.A.D.W.O.L.F. API as the icon for this shortcut. This
         * request may fail, so a backup resource is required.
         *
         * If a media ID is specified, this always takes precedent over a specified Icon, bitmap or
         * drawable.
         *
         * @param mediaID           Media ID for remote image file
         * @param backupIcon        Icon to use if this request fails
         * @return                  This builder object
         */
        public Builder setMediaID(int mediaID, IconCompat backupIcon) {
            this.mediaID = mediaID;
            this.backupIcon = backupIcon;
            return this;
        }

        /**
         * Set a drawable resource as the icon for this shortcut. This option will be ignored of a
         * mediaID is given or a direct bitmap is added.
         *
         * This field is ignored if a media ID, icon or bitmap is specified already.
         *
         * @param resourceID    ID of the drawable resource to use as icon background
         * @return              This builder object
         */
        public Builder setResourceID(int resourceID) {
            this.resourceID = resourceID;
            return this;
        }

        /**
         * Set a bitmap image as the icon for this shortcut.
         *
         * This field is ignored if a media ID or an Icon is already specified.
         *
         * @param bitmap        Bitmap image to use as icon background
         * @return              This builder object
         */
        public Builder setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        /**
         * Set the intent for when this shortcut is clicked.
         * {@see https://developer.android.com/reference/android/support/v4/content/pm/ShortcutInfoCompat.Builder.html#setIntent(android.content.Intent)}
         *
         * @param intent        Intent object
         * @return              This builder object
         */
        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Set the callback intent for when this shortcut was successfully pinned. If the user
         * rejects the pinning, this callback will *not* be called.
         *
         * @param callback      IntentSender object
         * @return              This builder object
         */
        public Builder setCallback(IntentSender callback) {
            this.callback = callback;
            return this;
        }

        /**
         * Build and request
         */
        public void build() {
            if(shortLabel == null) throw new IllegalStateException("Short label required");
            if(intent == null) throw
                    new IllegalStateException("Making a shortcut without an Intent makes no sense!");

            if(ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                if(mediaID > 0) {
                    Services.getInstance().mediaService
                            .file(mediaID, MediaAPI.MediaSize.LARGE.toString())
                            .enqueue(onMediaDownloaded);
                } else {
                    construct();
                }
            } else {
                Log.e(getClass().getSimpleName(),
                        "Requested building shortcut, but requesting shortcuts is " +
                                "not supported on this platform");
            }
        }

        private void construct() {
            ShortcutInfoCompat.Builder shortcutInfoBuilder =
                    new ShortcutInfoCompat.Builder(context, shortcutID)
                            .setShortLabel(shortLabel);
            shortcutInfoBuilder.setIntent(intent);
            shortcutInfoBuilder.setAlwaysBadged();

            if(longLabel != null) shortcutInfoBuilder.setLongLabel(longLabel);
            addIcon(shortcutInfoBuilder);

            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfoBuilder.build(), callback);
        }

        /**
         * Add the icon to the shortcut. Use defined order.
         *
         * @param builder   Builder object for shortcutinfoCompat
         */
        private void addIcon(ShortcutInfoCompat.Builder builder) {
            if (mediaID > 0) {
                if (bitmap != null) {
                    // Use bitmap as Icon
                    builder.setIcon(IconCompat.createWithAdaptiveBitmap(bitmap));
                } else if(backupIcon != null) {
                    builder.setIcon(backupIcon);
                } else if (backupBitmap != null) {
                    builder.setIcon(IconCompat.createWithAdaptiveBitmap(backupBitmap));
                } else if (backupResourceID > 0) {
                    builder.setIcon(IconCompat.createWithResource(context, backupResourceID));
                } else {
                    Log.e(getClass().getSimpleName(), "Media download failed and no backup specified. Using Wilson Logo");
                    builder.setIcon(IconCompat.createWithResource(context, R.drawable.icon));
                }
            } else if (icon != null) {
                builder.setIcon(icon);
            } else if (bitmap != null) {
                builder.setIcon(IconCompat.createWithAdaptiveBitmap(bitmap));
            } else if (resourceID > 0) {
                builder.setIcon(IconCompat.createWithResource(context, resourceID));
            } else {
                Log.e(getClass().getSimpleName(), "No icon specified. Using Wilson logo");
                builder.setIcon(IconCompat.createWithResource(context, R.drawable.icon));
            }
        }

        private Callback onMediaDownloaded = new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response != null && response.body() != null) {
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } else {
                    Log.e(getClass().getSimpleName(), "Received empty response for media id " + shortcutID + ". Falling back to fallback media");
                }
                construct();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Could not download media with id " + shortcutID + ". Falling back to fallback media");
                construct();
            }
        };

    }
}
