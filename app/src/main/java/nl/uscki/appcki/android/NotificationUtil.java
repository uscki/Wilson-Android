package nl.uscki.appcki.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;

import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;
import nl.uscki.appcki.android.services.NotificationType;

public class NotificationUtil extends ContextWrapper {

    public NotificationUtil(Context base) {
        super(base);
        createNotificationChannels();
    }

    private NotificationManager notificationManager;

    private static final String notificationIdentifierPrefix =
            "nl.uscki.appcki.android.notifications.";
    private static final String nameStringResourceSuffix = "name.";
    private static final String descriptionStringResourceSuffix = "description.";

    /**
     * The ACTIVITIES notification channel contains important updates
     * that (often) require action from the user
     */
    public static final String NOTIFICATION_CHANNEL_ACTIVITIES_ID = "nl.uscki.appcki.android.NOTIFICATIONS_ACTIVITIES";
    public static final int NOTIFICATION_CHANNEL_ACTIVITIES = 2;

    /**
     * The GENERAL notifications channel contains relatively important notifications
     * that are general to all USCKI members, such as shoutbox and news, but do not
     * require action (although actions could be permitted when e.g. a new poll is released)
     */
    public static final String NOTIFICATION_CHANNEL_GENERAL_ID = "nl.uscki.appcki.android.NOTIFICATIONS_GENERAL";
    public static final int NOTIFICATION_CHANNEL_GENERAL = 1;

    /**
     * The PERSONAL notification channel contains notifications on activities the user participated
     * in, such as agenda replies or forum updates
     */
    public static final String NOTIFICATION_CHANNEL_PERSONAL_ID = "nl.uscki.appcki.android.NOTIFICATIONS_PERSONAL";
    public static final int NOTIFICATION_CHANNEL_PERSONAL = 0;

    // Per the guides of https://material.io/design/platform-guidance/android-notifications.html#settings
    public static final HashMap<NotificationType, Integer> channelPriorities;
    static {
        channelPriorities = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            channelPriorities.put(NotificationType.achievement,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.agenda_announcement,
                    NotificationManager.IMPORTANCE_LOW);
            channelPriorities.put(NotificationType.agenda_new,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.agenda_reply,
                    NotificationManager.IMPORTANCE_LOW);
            channelPriorities.put(NotificationType.forum_new_topic,
                    NotificationManager.IMPORTANCE_LOW);
            channelPriorities.put(NotificationType.forum_reply,
                    NotificationManager.IMPORTANCE_LOW);
            channelPriorities.put(NotificationType.other,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.meeting_filledin,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.meeting_new,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.meeting_planned,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelPriorities.put(NotificationType.news,
                    NotificationManager.IMPORTANCE_DEFAULT);
        } else {
            channelPriorities.put(NotificationType.achievement,
                    NOTIFICATION_CHANNEL_PERSONAL);
            channelPriorities.put(NotificationType.agenda_announcement,
                    NOTIFICATION_CHANNEL_GENERAL);
            channelPriorities.put(NotificationType.agenda_new,
                    NOTIFICATION_CHANNEL_GENERAL);
            channelPriorities.put(NotificationType.agenda_reply,
                    NOTIFICATION_CHANNEL_PERSONAL);
            channelPriorities.put(NotificationType.forum_new_topic,
                    NOTIFICATION_CHANNEL_PERSONAL);
            channelPriorities.put(NotificationType.forum_reply,
                    NOTIFICATION_CHANNEL_PERSONAL);
            channelPriorities.put(NotificationType.other,
                    NOTIFICATION_CHANNEL_GENERAL);
            channelPriorities.put(NotificationType.meeting_filledin,
                    NOTIFICATION_CHANNEL_GENERAL);
            channelPriorities.put(NotificationType.meeting_new,
                    NOTIFICATION_CHANNEL_ACTIVITIES);
            channelPriorities.put(NotificationType.meeting_planned,
                    NOTIFICATION_CHANNEL_ACTIVITIES);
            channelPriorities.put(NotificationType.news,
                    NOTIFICATION_CHANNEL_GENERAL);
        }
    }

    /**
     * Try to create a notification channel for each notification type
     * we currently know of
     */
    public void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for(NotificationType nt : NotificationType.values()) {
                createChannel(nt);
            }
        }
    }

    /**
     * Get the channel for a notification of a certain type if the SDK version
     * allows it. Otherwise, post all notifications on the main channel
     *
     * @param notificationType      The notification type for which to get the channel
     * @return                      String with notification channel ID
     */
    public String getChannel(NotificationType notificationType) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return channelId(notificationType);
        } else {
            return "main";
        }
    }

    /**
     * Create a notification channel for a given notification type
     *
     * @param notificationType      Notification type
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(NotificationType notificationType) {

        // Find the ID of the string resource containing the name of the channel
        // for this type of notification
        int nameResourceId = getResources().getIdentifier(
                notificationIdentifierPrefix +
                        nameStringResourceSuffix +
                        notificationType.toString().toUpperCase(),
                "string",
                App.getContext().getPackageName()
        );

        if(nameResourceId <= 0) {
            Log.e(
                    getClass().toString(),
                    "Trying to create notification channel for type " +
                            notificationType.toString() +
                            " but no string resource for the name was found (" +
                            notificationIdentifierPrefix +
                            nameStringResourceSuffix +
                            notificationType.toString().toUpperCase() +
                            ")"
            );

            // Avoid crashes, but notifications for this channel will not be shown on this phone
            return;
        }

        // Required information for creating a channel
        String channelID = channelId(notificationType);
        String name = getResources().getString(nameResourceId);
        int importance = channelPriorities.get(notificationType);

        // Create the channel
        NotificationChannel channel = new NotificationChannel(
                channelID,
                name,
                importance
        );

        // Notification channel properties
        channel.setLightColor(Color.RED);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if(importance >= NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true);

        // See if a description for this channel exists as a string resource
        int descriptionResourceId = getResources().getIdentifier(
                notificationIdentifierPrefix +
                        descriptionStringResourceSuffix +
                        notificationType.toString().toUpperCase(),
                "string",
                App.getContext().getPackageName()
        );

        if(descriptionResourceId > 0) {
            String description = getResources().getString(descriptionResourceId);
            channel.setDescription(description);
        }

        // Finally, create the notification channel
        getManager().createNotificationChannel(channel);
    }

    /**
     * Get the channel ID for a notification type
     *
     * Modify with care
     *
     * @param notificationType      Notification type
     * @return                      Channel ID
     */
    public static String channelId(NotificationType notificationType) {
        return "" + notificationType.toString().toUpperCase();
    }

    /**
     * Add sound, vibration and notification LED effects based on the users preference for this type
     * of notification
     * @param notification      Base notification to which effects should be added
     * @param notificationType  Type of the notification as received
     */
    public void addNotificationPropertiesBySettings(NotificationCompat.Builder notification, NotificationType notificationType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Since Oreo, these things are handled by notification channels.
            // Continuing will crash the application
            return;
        }

        // Build a base string for the preferences we want to find
        String basePreferenceKey = "notifications_";
        int priority = channelPriorities.get(notificationType);
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
            notification.setSound(Uri.parse(ringtoneUri));
        } else {
            notification.setSound(null);
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
                notification.setVibrate(vibrationPattern);
            }
        }

        // Add notification LED effects
        if(prefs.getBoolean(basePreferenceKey + "show_light", true)) {
            if(prefs.getBoolean(basePreferenceKey + "led_mode", true)) {
                // Show light as just on
                notification.setLights(Color.RED, 1000, 0);
            } else {
                // Blink once per second
                notification.setLights(Color.RED, 150, 1000);
            }
        }
    }

    /**
     * Since android oreo uses notification channels, from which no vibration pattern can be selected
     * we have to get create
     *
     * Using this function, vibration can be triggered from this application when a notification
     * is received, but this should only be done if the system won't also vibrate and the user
     * has indicated they want this to happen
     *
     * @param notificationType  Type of received notification
     */
    public void vibrateIfEnabled(NotificationType notificationType) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // See if custom vibration patterns is enabled
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
            boolean vibrateFromApp = prefs.getBoolean("notifications_oreo_vibrate", true);

            if(!vibrateFromApp)
                // User doesn't want us to do this
                return;

            // Check if vibration is already handled by the notification channel
            NotificationManager manager = getManager();
            NotificationChannel channel = manager.getNotificationChannel(channelId(notificationType));
            if(channel.shouldVibrate())
                // Vibration is already handled by the channel. Vibrating here would just
                // be conflicting
                return;

            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if(v != null) {
                VibrationPatternPreferenceHelper vibrationHelper =
                        new VibrationPatternPreferenceHelper();

                int selectedVibrationPatternIndex =
                        vibrationHelper.getIndexOfVibrationPatternPreference(
                                "notifications_oreo_vibration_pattern");

                if(selectedVibrationPatternIndex < 0) {
                    Log.e(getClass().toString(), "Index of selected pattern in notifications_oreo_vibration_pattern not found");
                    return;
                }

                int vibrationPatternResourceId =
                        vibrationHelper.getVibrationPatternResourceIdAtIndex(
                                selectedVibrationPatternIndex);

                if(vibrationPatternResourceId <= 0)
                    // Not found
                    return;

                // Load the preferred vibration pattern
                long[] pattern = vibrationHelper.getVibrationPattern(vibrationPatternResourceId);
                VibrationEffect vEffect = VibrationEffect.createWaveform(pattern, -1);

                // Vibrate the loaded vibration effect
                if(isVibrationEnabled()) {
                    v.vibrate(vEffect);
                }
            }
        }
    }

    /**
     * Check if vibration is currently active.
     * Gotta love stack overflow
     * https://stackoverflow.com/questions/32709260/android-detect-if-the-vibrate-setting-in-the-device-is-on-or-off-esp-for-the-c
     * @return  Boolean indicating vibration status
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isVibrationEnabled() {
        boolean vibrationStatus = false;
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            //ensuring it is not on silent
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                vibrationStatus = true;
            } else if(1 == Settings.System.getInt(getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 0)) {
                vibrationStatus = true;
            }
        }
        return vibrationStatus;
    }

    /**
     * Get an instance of the notification manager
     * @return NotificationManager
     */
    private NotificationManager getManager() {
        if(notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

}
