package nl.uscki.appcki.android.helpers.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.services.NotificationType;

public class EmptyNotification extends BadWolfNotification {

    public EmptyNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.other;
    }

    @Override
    protected Intent getIntent() {
        return null;
    }

    @Override
    protected String getBackstackAction() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_REMINDER;
    }

    @Override
    protected void addActions() {

    }
}
