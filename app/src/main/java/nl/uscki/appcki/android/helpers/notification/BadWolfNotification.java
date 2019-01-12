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
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;
import nl.uscki.appcki.android.helpers.notification.achievement.AchievementNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaAnnouncementNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaFromBackupNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaNewNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaReplyNotification;
import nl.uscki.appcki.android.helpers.notification.bugtracker.BugTrackerCommentNotification;
import nl.uscki.appcki.android.helpers.notification.bugtracker.BugTrackerNewNotification;
import nl.uscki.appcki.android.helpers.notification.bugtracker.BugTrackerStatusChangedNotification;
import nl.uscki.appcki.android.helpers.notification.forum.ForumNewTopicNotification;
import nl.uscki.appcki.android.helpers.notification.forum.ForumReplyNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingFilledInNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingNewNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingPlannedNotification;
import nl.uscki.appcki.android.helpers.notification.news.NewsNotification;
import nl.uscki.appcki.android.services.NotificationType;

public abstract class BadWolfNotification {

    public static final String PARAM_NOTIFICATION_TITLE = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TITLE";
    public static final String PARAM_NOTIFICATION_CONTENT = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_CONTENT";
    public static final String PARAM_NOTIFICATION_TAG = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TAG";
    public static final String PARAM_NOTIFICATION_ID = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_ID";
    public static final String PARAM_VIEW_ITEM_ID = "nl.uscki.appcki.android.services.extra.PARAM_VIEW_ITEM_ID";

    protected Context context;
    protected String title;
    protected String content;
    protected int id;
    protected int notification_id;
    protected int item_id;
    protected String tag;
    protected NotificationCompat.Builder notificationBuilder;
    protected NotificationUtil u;

    public static BadWolfNotification fromFirebaseMessage(Context c, RemoteMessage message) {
        BadWolfNotification n;

        try {
            switch (message.getData().get("type")) {
                case "achievement":
                    n = new AchievementNotification(c, message);
                    break;
                case "agenda_announcement":
                    n = new AgendaAnnouncementNotification(c, message);
                    break;
                case "agenda_from_backup":
                    n = new AgendaFromBackupNotification(c, message);
                    break;
                case "agenda_new":
                    n = new AgendaNewNotification(c, message);
                    break;
                case "agenda_reply":
                    n = new AgendaReplyNotification(c, message);
                    break;
                case "bugtracker_comment":
                    n = new BugTrackerCommentNotification(c, message);
                    break;
                case "bugtracker_new":
                    n = new BugTrackerNewNotification(c, message);
                    break;
                case "bugtracker_status_changed":
                    n = new BugTrackerStatusChangedNotification(c, message);
                    break;
                case "forum_new_topic":
                    n = new ForumNewTopicNotification(c, message);
                    break;
                case "forum_reply":
                    n = new ForumReplyNotification(c, message);
                    break;
                case "meeting_filledin":
                    n = new MeetingFilledInNotification(c, message);
                    break;
                case "meeting_new":
                    n = new MeetingNewNotification(c, message);
                    break;
                case "meeting_planned":
                    n = new MeetingPlannedNotification(c, message);
                    break;
                case "news":
                    n = new NewsNotification(c, message);
                    break;
                case "other":
                    // Intentionally no break!
                default:
                    n = new EmptyNotification(c, message);
                    break;
            }
        } catch(Exception e) {
            n = new EmptyNotification(c, message);
        }

        return n;
    }

    /**
     * Create a notification from the remote message body received by FireBase
     * @param c         Context
     * @param message   Message as received from FireBase
     */
    public BadWolfNotification(Context c, RemoteMessage message) {
        this.context = c;
        this.title = message.getData().get("title");
        this.content = message.getData().get("content");
        this.id = Integer.parseInt(message.getData().get("id"));
        this.notification_id = id;
        this.item_id = id;
        this.tag = getNotificationType().toString();
        build();
    }

    /**
     * Construct a notification from a previously used notification intent. This populates
     * the object with previously stored information
     * @param c         Context
     * @param intent    Intent this notification should be built from
     */
    public BadWolfNotification(Context c, Intent intent) {
        // Extract information from existing intent
        this.context = c;
        this.title = intent.getStringExtra(PARAM_NOTIFICATION_TITLE);
        this.content = intent.getStringExtra(PARAM_NOTIFICATION_CONTENT);
        this.notification_id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        this.item_id = intent.getIntExtra(PARAM_VIEW_ITEM_ID, -1);
        this.tag = intent.getStringExtra(PARAM_NOTIFICATION_TAG);
        build();

        // Make sure this notification doesn't make a sound or vibrate
        this.notificationBuilder.setOnlyAlertOnce(true);
    }

    /**
     * Show the notification
     */
    public void show() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this.context);
        manager.notify(this.getClass().toString(), this.notification_id, this.notificationBuilder.build());

        // TODO except if it is updated?
        u.vibrateIfEnabled(getNotificationType());
    }

    /**
     * Return the type for this notification.
     * // TODO this could be simplified later on by using implementing class, but small steps
     * @return
     */
    protected abstract NotificationType getNotificationType();

    /**
     * Get the intent for this notification. May be null
     * @return
     */
    protected abstract Intent getIntent();

    /**
     * Get the backstack action that should be put on the main intent of this notification.
     * This is an action identifier (e.g. MainActivity.ACTION_MEETING_OVERVIEW) that can be parsed
     * by an activity to show a specific screen. The screen in question is the screen the user
     * should be redirected to when pressing the back button after opening this notification
     * @return  Backstack action
     */
    protected abstract String getBackstackAction();

    /**
     * Get the category for this notification (e.g. Notification.CATEGORY_EVENT)
     * @return  String
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected abstract String getNotificationCategory();

    /**
     * Add action buttons to this notification. May be empty method if no actions are present
     */
    protected abstract void addActions();

    /**
     * Put extra's to a notification intent that help reproduce or overwrite a notification
     * @param intent    Intent to add extras to
     * @return          Same intent with added extras
     */
    protected Intent addReproducabilityExtras(Intent intent) {
        // Add info so notification can be recreated with same info
        intent.putExtra(PARAM_NOTIFICATION_ID, this.id);
        intent.putExtra(PARAM_NOTIFICATION_TAG, this.getClass().toString()); // TODO is this inherited?
        intent.putExtra(PARAM_NOTIFICATION_TITLE, this.title);
        intent.putExtra(PARAM_NOTIFICATION_CONTENT, this.content);
        intent.putExtra(PARAM_VIEW_ITEM_ID, this.id); // In future versions, this may point to something else

        return intent;
    }

    /**
     * Build the entire notification
     */
    private void build() {
        u = new NotificationUtil(this.context);
        buildBaseNotification();
        addIntention();
        addActions();
    }

    /**
     * Build the base notification, containing only application identification stuff
     */
    private void buildBaseNotification() {
        notificationBuilder = new NotificationCompat.Builder(this.context, this.u.getChannel(getNotificationType()));
        notificationBuilder.setContentTitle(this.title);
        notificationBuilder.setContentText(this.content);
        notificationBuilder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(getNotificationCategory());
        }
        setNotificationIcons();

        u.addNotificationPropertiesBySettings(notificationBuilder, getNotificationType());
    }

    /**
     * Add icons to the notification
     */
    private void setNotificationIcons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = Utils.getBitmapFromVectorDrawable(this.context, R.drawable.ic_wilson);
            notificationBuilder.setLargeIcon(bm);
        }
        notificationBuilder.setSmallIcon(R.drawable.cki_logo_white);
    }

    /**
     * Add an intention to the notification, based on what is specified by the implementing class
     */
    private void addIntention() {
        Intent i = getIntent();

        // It is allowed to have notifications which do nothing, although we frown upon this
        if(i == null) return;

        i = addReproducabilityExtras(i);

        // Make sure the intent always has an action
        if(i.getAction() == null) i.setAction(Intent.ACTION_VIEW);

        // A pending intent is what ultimately opens the app
        PendingIntent pendingIntent;

        if(getBackstackAction() == null || getBackstackAction().equals("")) {
            pendingIntent = PendingIntent.getActivity(this.context, 0, i, 0);
        } else {
            // TODO this may become a problem if an action from a different activity than the main activity is used
            Intent backPressedIntent = new Intent(this.context, MainActivity.class);
            backPressedIntent.setAction(getBackstackAction());

            pendingIntent = TaskStackBuilder.create(this.context)
                    .addNextIntent(backPressedIntent)
                    .addNextIntent(i)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        notificationBuilder.setContentIntent(pendingIntent);
    }

    private void addPropertiesAccordingToSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Since Oreo, these things are handled by notification channels.
            // Continuing will crash the application
            return;
        }

        // TODO these channel properties could also just be abstract methods which return the priority for that noficiation. Use ENUM!

        // Build a base string for the preferences we want to find
        String basePreferenceKey = "notifications_";
        int priority = NotificationUtil.channelPriorities.get(getNotificationType());
        if(priority == 0) {
            basePreferenceKey += "interactive_";
        } else if(priority == 1){
            basePreferenceKey += "general_";
        } else if (priority == 2) {
            basePreferenceKey += "personal_";
        } else {
            // We don't have settings for this
            Log.e(getClass().toString(),
                    "Notification priority " + priority +
                            " does not exists on the pre-oreo channels!");
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        // Add notification sound effects
        if(prefs.getBoolean(basePreferenceKey + "new_message", true)) {
            String ringtoneUri = prefs.getString(basePreferenceKey + "new_message_ringtone",
                    RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
            notificationBuilder.setSound(Uri.parse(ringtoneUri));
        } else {
            notificationBuilder.setSound(null);
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
                notificationBuilder.setLights(Color.RED, 1000, 0);
            } else {
                // Blink once per second
                notificationBuilder.setLights(Color.RED, 150, 1000);
            }
        }
    }

}
