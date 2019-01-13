package nl.uscki.appcki.android.services;


/**
 * These notification channels can be used in older versions of Android, where channels
 * are not yet supported by the operating system. These channels are not actually created, but
 * emulate the required behavior, including allowing changing the notification settings for each
 * channel independently
 */
public enum CompatNotificationChannel{
    INTERACTIVE("nl.uscki.appcki.android.NOTIFICATIONS_ACTIVITIES", "interactive_"),
    GENERAL("nl.uscki.appcki.android.NOTIFICATIONS_GENERAL", "general_"),
    PERSONAL("nl.uscki.appcki.android.NOTIFICATIONS_PERSONAL", "personal_");

    private String channelID;
    private String basePreferenceKey;

    CompatNotificationChannel(String channelID, String basePreferenceKey) {
        this.channelID = channelID;
        this.basePreferenceKey = basePreferenceKey;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getBasePreferenceKey() {
        return basePreferenceKey;
    }
}
