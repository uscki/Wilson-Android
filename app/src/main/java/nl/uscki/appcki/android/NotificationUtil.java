package nl.uscki.appcki.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

public class NotificationUtil extends ContextWrapper {

    public NotificationUtil(Context base) {
        super(base);
        createNotificationChannels();
    }

    private NotificationManager notificationManager;

    /**
     * The ACTIVITIES notification channel contains important updates
     * that (often) require action from the user
     */
    public static final String NOTIFICATION_CHANNEL_ACTIVITIES_ID = "nl.uscki.appcki.android.NOTIFICATIONS_ACTIVITIES";

    /**
     * The GENERAL notifications channel contains relatively important notifications
     * that are general to all USCKI members, such as shoutbox and news, but do not
     * require action (although actions could be permitted when e.g. a new poll is released)
     */
    public static final String NOTIFICATION_CHANNEL_GENERAL_ID = "nl.uscki.appcki.android.NOTIFICATIONS_GENERAL";

    /**
     * The PERSONAL notification channel contains notifications on activities the user participated
     * in, such as agenda replies or forum updates
     */
    public static final String NOTIFICATION_CHANNEL_PERSONAL_ID = "nl.uscki.appcki.android.NOTIFICATIONS_PERSONAL";


    public void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel activitiesChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ACTIVITIES_ID,
                    getString(R.string.notification_channel_activities_name),
                    NotificationManager.IMPORTANCE_HIGH
            );

            activitiesChannel.enableLights(true);
            activitiesChannel.enableVibration(true);
            activitiesChannel.setLightColor(Color.RED);
            activitiesChannel.setDescription(getString(R.string.notification_channel_activities_description));
            activitiesChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationChannel generalChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_GENERAL_ID,
                    getString(R.string.notification_channel_general_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            generalChannel.enableLights(true);
            generalChannel.enableVibration(true);
            generalChannel.setLightColor(Color.RED);
            generalChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            generalChannel.setDescription(getString(R.string.notification_channel_general_description));

            NotificationChannel personalChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_PERSONAL_ID,
                    getString(R.string.notification_channel_personal_name),
                    NotificationManager.IMPORTANCE_LOW
            );

            personalChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            personalChannel.setDescription(getString(R.string.notification_channel_personal_description));
            personalChannel.enableLights(false);
            personalChannel.enableVibration(true);
            personalChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(activitiesChannel);
            getManager().createNotificationChannel(generalChannel);
            getManager().createNotificationChannel(personalChannel);
        }
    }

    public String getChannel(String ChannelId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ChannelId;
        } else {
            return "main";
        }
    }


    private NotificationManager getManager() {
        if(notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

}
