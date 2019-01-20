package nl.uscki.appcki.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;
import nl.uscki.appcki.android.services.NotificationType;

public class NotificationUtil extends ContextWrapper {

    public NotificationUtil(Context base) {
        super(base);
        if(PermissionHelper.hasAgreedToNotificationPolicy(base)) {
            // Only create notification channels if the user has agreed to collecting a personal
            // identifier to send notifications to this device
            createNotificationChannels();
        }
    }

    private NotificationManager notificationManager;

    private static final String notificationIdentifierPrefix =
            "nl.uscki.appcki.android.notifications.";
    private static final String nameStringResourceSuffix = "name.";
    private static final String descriptionStringResourceSuffix = "description.";


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
     * Remove existing notification channels
     */
    public void removeExistingChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for(NotificationType nt : NotificationType.values()) {
                getManager().deleteNotificationChannel(getChannel(nt));
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
        int importance = notificationType.getImportance();

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
        return notificationType.toString().toUpperCase();
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
            if(channel != null && channel.shouldVibrate())
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

    /**
     * Enable or disable generation of tokens for the firebase messaging service. This is disabled
     * by default, but needs to be enabled to receive notifications.
     *
     * This value persists through app restarts, according to the documentation, so make sure to
     * not use this lightly:
     * https://firebase.google.com/docs/cloud-messaging/android/client#prevent-auto-init
     *
     * @param enabled   True to enable services, false to disable services
     */
    public static void setFirebaseEnabled(boolean enabled) {
        Log.d(NotificationUtil.class.getSimpleName(), "Setting initialization of firebase to " + enabled);
        FirebaseMessaging.getInstance().setAutoInitEnabled(enabled);
    }

}
