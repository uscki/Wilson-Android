package nl.uscki.appcki.android.helpers.notification;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;

public class EmptyNotification extends BadWolfNotification {

    public EmptyNotification(Context c, RemoteMessage message) {
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
