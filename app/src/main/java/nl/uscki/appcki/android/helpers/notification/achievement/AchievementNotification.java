package nl.uscki.appcki.android.helpers.notification.achievement;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.helpers.notification.AbstractNotification;

public class AchievementNotification extends AbstractNotification {
    public AchievementNotification(Context c, RemoteMessage message) {
        super(c, message);
    }


    @Override
    protected Intent getNotificationIntent() {
        return null;
    }


    @Override
    protected void addActions() {

    }
}
