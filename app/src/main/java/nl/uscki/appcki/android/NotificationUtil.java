package nl.uscki.appcki.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;

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
        channel.enableVibration(true);
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

    // TODO: Write function for adding ringtone, vibrate, etc on a notification of a type, based on settings
    // and based on the channel (for versions less than oreo) based on the array above

    private NotificationManager getManager() {
        if(notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

}
