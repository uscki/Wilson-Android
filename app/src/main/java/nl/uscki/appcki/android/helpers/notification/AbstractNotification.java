package nl.uscki.appcki.android.helpers.notification;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;
import nl.uscki.appcki.android.services.NotificationType;

public abstract class AbstractNotification {

    public static final String PARAM_NOTIFICATION_TITLE = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TITLE";
    public static final String PARAM_NOTIFICATION_CONTENT = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_CONTENT";
    public static final String PARAM_NOTIFICATION_TAG = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TAG";
    public static final String PARAM_NOTIFICATION_TYPE = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TYPE";
    public static final String PARAM_NOTIFICATION_ID = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_ID";
    public static final String PARAM_VIEW_ITEM_ID = "nl.uscki.appcki.android.services.extra.PARAM_VIEW_ITEM_ID";

    protected Context context;
    protected NotificationType type;

    // Properties sent by server
    protected int id;
    protected String title;
    protected String content;

    // Properties to distinguish this notification from others shown in the system
    protected int notification_id;
    protected int item_id;
    protected String tag;

    // Utility properties
    protected NotificationCompat.Builder notificationBuilder;
    protected NotificationUtil notificationUtil;
    private boolean silent;

    /**
     * Create a AbstractNotification from a received Firebase message
     * @param c           A context
     * @param message     Firebase message
     * @return            AbstractNotification
     */
    public static AbstractNotification fromFirebaseMessage(Context c, RemoteMessage message) {
        AbstractNotification n;

        try {
            NotificationType type = NotificationType.valueOf(message.getData().get("type"));
            n = type.getClazz().getConstructor(Context.class, RemoteMessage.class).newInstance(c, message);
            n.type = type;
        } catch (Exception e) {
            n = new EmptyNotification(c, message);
        }

        n.build();

        return n;
    }

    /**
     * Create a notification from the remote message body received by FireBase
     * @param context   Context
     * @param message   Message as received from FireBase
     */
    public AbstractNotification(Context context, RemoteMessage message) {
        this.context = context;
        parseType(message.getData().get("type"));
        this.title = message.getData().get("title");
        this.content = message.getData().get("content");
        this.id = Integer.parseInt(message.getData().get("id"));
        this.notification_id = this.id;
        this.item_id = this.id;
        this.tag = message.getData().get("type");
        this.silent = false;
    }

    /**
     * Construct a notification from a previously used notification intent. This populates
     * the object with previously stored information
     * @param c         Context
     * @param intent    Intent this notification should be built from
     */
    public AbstractNotification(Context c, Intent intent) {
        // Extract information from existing intent
        this.context = c;
        parseType(intent.getStringExtra(PARAM_NOTIFICATION_TYPE));
        this.title = intent.getStringExtra(PARAM_NOTIFICATION_TITLE);
        this.content = intent.getStringExtra(PARAM_NOTIFICATION_CONTENT);
        this.id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        this.notification_id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        this.item_id = intent.getIntExtra(PARAM_VIEW_ITEM_ID, -1);
        this.tag = intent.getStringExtra(PARAM_NOTIFICATION_TAG);
        this.silent = true;
    }

    /**
     * Before the notification can be shown, it needs to be built. If the constructor, instead
     * of one of the static create methods, is used, or if some properties on this object are
     * changed after construction, this method needs to be called explicitly.
     */
    public void build() {
        this.notificationUtil = new NotificationUtil(this.context);
        buildBaseNotification();
        addPropertiesAccordingToSettings();
        addIntention();
        addActions();

        if(silent) {
            // Make sure this notification doesn't make a sound or vibrate
            this.notificationBuilder.setOnlyAlertOnce(true);
        }
    }

    /**
     * Show the notification
     */
    public void show() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this.context);
        manager.notify(this.getClass().toString(), this.notification_id, this.notificationBuilder.build());

        if(!silent) {
            notificationUtil.vibrateIfEnabled(this.type);
        }
    }

    /**
     * Set this notification to show silently, i.e. without making a sound or vibrating
     * @param silent    Boolean indicating whether the system should alert the user to this
     *                  notification
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * Constructs an intent for this notification, dictating the behavior when the notification
     * is clicked
     * @return  Intent to be used when this notification is clicked
     */
    protected abstract Intent getNotificationIntent();

    /**
     * Add action buttons to this notification, or perform other operations specific to the type
     * of notification
     */
    protected abstract void addActions();

    /**
     * Put extra's to a notification intent that help reproduce or overwrite a notification
     * @param intent    Intent to add extras to
     * @return          Same intent with added extras
     */
    protected Intent addReproducabilityExtras(Intent intent) {
        intent.putExtra(PARAM_NOTIFICATION_ID, this.id);
        intent.putExtra(PARAM_NOTIFICATION_TAG, this.getClass().toString());
        intent.putExtra(PARAM_NOTIFICATION_TITLE, this.title);
        intent.putExtra(PARAM_NOTIFICATION_CONTENT, this.content);
        intent.putExtra(PARAM_NOTIFICATION_TYPE, this.type.toString());

        // In future versions, the following may point to something else
        intent.putExtra(PARAM_VIEW_ITEM_ID, this.id);

        return intent;
    }

    /**
     * Build the base notification, containing only application identification stuff
     */
    private void buildBaseNotification() {
        this.notificationBuilder = new NotificationCompat.Builder(this.context, this.notificationUtil.getChannel(this.type));
        this.notificationBuilder.setContentTitle(this.title);
        this.notificationBuilder.setContentText(this.content);
        this.notificationBuilder.setAutoCancel(true);
        this.notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(this.content));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.notificationBuilder.setCategory(this.type.getCategory());
        }
        setNotificationIcons();
    }

    /**
     * Add icons to the notification
     */
    private void setNotificationIcons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = Utils.getBitmapFromVectorDrawable(this.context, R.drawable.ic_wilson);
            this.notificationBuilder.setLargeIcon(bm);
        }
        this.notificationBuilder.setSmallIcon(R.drawable.cki_logo_white);
    }

    /**
     * Add an intention to the notification, based on what is specified by the implementing class
     */
    private void addIntention() {
        Intent i = getNotificationIntent();

        // It is allowed to have notifications which do nothing, although we frown upon this
        if(i == null) return;

        i = addReproducabilityExtras(i);

        // Make sure the intent always has an action
        if(i.getAction() == null) i.setAction(Intent.ACTION_VIEW);

        // A pending intent is what ultimately opens the app
        PendingIntent pendingIntent;

        String backstackAction = this.type.getBackstackAction();

        if(backstackAction == null || backstackAction.equals("")) {
            pendingIntent = PendingIntent.getActivity(this.context, 0, i, 0);
        } else {
            // TODO this may become a problem if an action from a different activity than the main activity is used
            Intent backPressedIntent = new Intent(this.context, MainActivity.class);
            backPressedIntent.setAction(backstackAction);

            pendingIntent = TaskStackBuilder.create(this.context)
                    .addNextIntent(backPressedIntent)
                    .addNextIntent(i)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        this.notificationBuilder.setContentIntent(pendingIntent);
    }

    private void addPropertiesAccordingToSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Since Oreo, these things are handled by notification channels.
            // Continuing will crash the application
            return;
        }

        // Build a base string for the preferences we want to find
        String basePreferenceKey = "notifications_" + this.type.getChannel().getBasePreferenceKey();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        // Add notification sound effects
        if(prefs.getBoolean(basePreferenceKey + "new_message", true)) {
            String ringtoneUri = prefs.getString(basePreferenceKey + "new_message_ringtone",
                    RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
            this.notificationBuilder.setSound(Uri.parse(ringtoneUri));
        } else {
            this.notificationBuilder.setSound(null);
        }

        // Add vibration effects
        if(prefs.getBoolean(basePreferenceKey + "new_message_vibrate", true)) {
            VibrationPatternPreferenceHelper vibrationHelper =
                    new VibrationPatternPreferenceHelper();

            int vibrationPatternIndex = vibrationHelper.getIndexOfVibrationPatternPreference(
                    basePreferenceKey + "vibration_pattern");

            int vibrationPatternResourceId = vibrationHelper
                    .getVibrationPatternResourceIdAtIndex(vibrationPatternIndex);

            long[] vibrationPattern = vibrationHelper
                    .getVibrationPattern(vibrationPatternResourceId);

            if(vibrationPattern != null) {
                notificationBuilder.setVibrate(vibrationPattern);
            }
        }

        // Add notification LED effects
        if(prefs.getBoolean(basePreferenceKey + "show_light", true)) {
            if(prefs.getBoolean(basePreferenceKey + "led_mode", true)) {
                // Show light as just on
                this.notificationBuilder.setLights(Color.RED, 1000, 0);
            } else {
                // Blink once per second
                this.notificationBuilder.setLights(Color.RED, 150, 1000);
            }
        }
    }

    /**
     * Try to parse the type of notification from the name encoded as a string. If the name is not
     * recognized, the type NotificationType.other is always used
     * @param type  String containing the name of a NotificationType
     */
    private void parseType(String type) {
        try {
            this.type = NotificationType.valueOf(type);
        } catch(IllegalArgumentException e) {
            this.type = NotificationType.other;
            Log.e(getClass().getSimpleName(), "Trying to create notification for nonexistent type " + type);
        }
    }

}
